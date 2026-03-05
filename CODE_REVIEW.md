# Code Review - Expense Pulse (Backend + Frontend)

**Data:** 2026-03-03
**Branch:** `ottimizzazioni`
**Backend:** Quarkus 3.30.4 + MongoDB Panache
**Frontend:** Vue 3.5 + TypeScript + Vite 7 + Pinia + TailwindCSS

---

## Sommario

- [Backend - Bug Critici](#backend---bug-critici)
- [Backend - Bug Importanti](#backend---bug-importanti)
- [Backend - Qualita del Codice](#backend---qualità-del-codice)
- [Backend - Architettura](#backend---architettura)
- [Backend - Sicurezza](#backend---sicurezza)
- [Backend - Performance](#backend---performance)
- [Backend - Testing](#backend---testing)
- [Backend - Configurazione](#backend---configurazione)
- [Backend - API Design](#backend---api-design)
- [Frontend - Bug Critici](#frontend---bug-critici)
- [Frontend - Bug Importanti](#frontend---bug-importanti)
- [Frontend - Qualita del Codice](#frontend---qualità-del-codice)
- [Frontend - Architettura](#frontend---architettura)
- [Frontend - Sicurezza](#frontend---sicurezza)
- [Frontend - Performance](#frontend---performance)
- [Frontend - TypeScript](#frontend---typescript)
- [Frontend - Integrazione API](#frontend---integrazione-api)
- [Frontend - UI/UX](#frontend---uiux)
- [Raccomandazioni Prioritarie](#raccomandazioni-prioritarie)

---

# BACKEND (Quarkus)

## Backend - Qualità del Codice


### CQ-5: Naming inconsistente - mix italiano/inglese

- Italiano: `elenco()`, `crea()`, `cancella()`, `aggiornaCategory()`
- Inglese: `createTransaction()`, `deleteLayout()`, `getAllLayouts()`

---

### CORRETTO nel primo e terzo file. Il secondo file è da rivedere
### CQ-7: Codice commentato in produzione

| File | Righe |
|------|-------|
| `LayoutServiceImpl.java:57-70` | Intero metodo `findDefaultLayout()` |
| `LayoutApi.java:92-95` | Logica `if ("default".equals(id))` |
| `LayoutService.java:12` | Dichiarazione `findDefaultLayout()` |

---


## Backend - Architettura
### CORRETTO
### ARCH-1: Nessun ExceptionMapper

Non esiste un `ExceptionMapper<ApplicationException>` o `ExceptionMapper<NotFoundException>`. Le eccezioni risultano in risposte 500 generiche senza body strutturato. I 404 dichiarati nella documentazione OpenAPI non vengono mai effettivamente ritornati.

  3 file nuovi:                                                                                                                                                                                                                     
  - ErrorResponse.java — DTO con codice e messaggio per body JSON strutturato                                                                                                                                                       
  - NotFoundExceptionMapper.java — @Provider, mappa NotFoundException → HTTP 404 con body {"codice":"404_NOT_FOUND","messaggio":"..."}                                                                                              
  - ApplicationExceptionMapper.java — @Provider, mappa ApplicationException → HTTP 500 con body {"codice":"INTERNAL_SERVER_ERROR","messaggio":"..."}                                                                                

  Propagazione NotFoundException (non più ingoiata dai catch-all):                                                                                                                                                                  
                                                                                                                                                                                                                                    
  - CategoryServiceImpl — i 4 casi "non trovata" ora lanciano NotFoundException invece di ServiceException, con catch(NotFoundException nfe) { throw nfe; } prima del catch(Exception)                                              
  - TransactionsServiceImpl — i 2 casi "non trovato" stessa cosa                                                                                                                                                                    
  - LayoutServiceImpl — catch(NotFoundException nfe) { throw nfe; } aggiunto in findLayoutByName, findLayoutById, updateLayout, deleteLayout; findByFiltro ora lancia NotFoundException
  - CategoryApi — aggiornaCategory e deleteCategory rilanciano NotFoundException
  - TransactionsApi — aggiornaTransaction e deleteTransaction rilanciano NotFoundException

  Ora i 404 dichiarati nelle annotation OpenAPI vengono effettivamente ritornati come HTTP 404 con body JSON strutturato.

---
### CORRETTO
### ARCH-2: Nessuna validazione input

Zero Bean Validation (`@NotNull`, `@NotBlank`, `@Size`, `@Valid`) su qualsiasi DTO o parametro API. Manca la dipendenza `quarkus-hibernate-validator` nel `pom.xml`.

Esempi di rischi:
- `CategoryDTO.descrizione`, `codice` possono essere null/blank
- `TransactionDTO.title`, `amount`, `date` possono essere null
- `PaginazioneDTO.numeroPagina` e `numeroElementiPerPagina` possono essere null (NPE in `TransactionRepository.java:96`)

Correzioni fatte:
Step 1 — pom.xml: Added quarkus-hibernate-validator dependency.                                                                                                                                                                   
                                                                                                                                                                                                                                    
  Step 2 — DTO annotations:                                                                                                                                                                                                         
  - CategoryDTO: @NotBlank on descrizione/codice, @NotNull @PositiveOrZero on budget                                                                                                                                                
  - TransactionDTO: @NotBlank on title/date, @NotNull @Positive on amount, @Valid on category
  - PaginazioneDTO: @NotNull @Min(0) on numeroPagina, @NotNull @Min(1) on numeroElementiPerPagina                                                                                                                                   
  - FiltroRicercaTransactionDTO: @Valid on paginazione                                                                                                                                                                              
  - LayoutDTO: @NotBlank on layoutName, @Valid on layoutItems                                                                                                                                                                       
  - LayoutItemDTO: @NotBlank on i, @NotNull on x/y/w/h                                                                                                                                                                              

  Step 3 — @Valid on API endpoints:
  - CategoryApi: creaCategory, aggiornaCategory
  - TransactionsApi: ricercaTransactions, creaTransaction, aggiornaTransaction
  - LayoutApi: getDefaultLayout, resetLayout, createLayout, updateLayout

  Quarkus REST restituirà automaticamente HTTP 400 con il dettaglio delle violazioni per qualsiasi input non valido.

---
### CORRETTO
### ARCH-3: Stack trace persi nel exception handling

Pattern ricorrente in tutto il codebase:

```java
catch(Exception e) {
    throw new ServiceException(e.getMessage());  // Perde lo stack trace originale
}
```

Usare `new ServiceException(e.getMessage(), e)` per preservare la causa.
 Compila correttamente. Ecco il riepilogo delle correzioni su 3 file:                                                                                                                                                              
                                                                                                                                                                                                                                    
  CategoryServiceImpl.java — 7 fix:                                                                                                                                                                                                 
  - catch(ServiceException se) in findCategoryByCodice e findById: throw new ServiceException(se.getMessage()) → throw se;                                                                                                          
  - elenco(): aggiunto , ex come causa                                                                                                                                                                                              
  - crea(): aggiunto , e su MongoWriteException e , ex su Exception                                                                                                                                                                 
  - aggiornaCategory(): aggiunto , e su MongoWriteException else branch e , ex su Exception
  - cancella(): aggiunto , ex come causa                                                                                                                                                                                            
                                                                                                                                                                                                                                    
  TransactionsServiceImpl.java — 2 fix:                                                                                                                                                                                             
  - catch(ServiceException se) in aggiornaTransaction e cancella: throw new ServiceException(se.getMessage()) → throw se;                                                                                                           

  LayoutServiceImpl.java — 8 fix:
  - Tutti e 6 i metodi (findLayoutByName, findLayoutById, createLayout, updateLayout, deleteLayout, getAllLayouts): aggiunto , e come causa
  - findByFiltro: catch(ServiceException se) → throw se; + aggiunto , e nel catch generico
---
### CORRETTO
### ARCH-4: `@Model` su TransactionsServiceImpl

**File:** `TransactionsServiceImpl.java:20`

`@Model` combina `@Named` + `@RequestScoped`, creando una nuova istanza per ogni richiesta HTTP. Tutti gli altri service usano `@ApplicationScoped`. Inconsistente e meno performante.

---
### CORRETTO
### ARCH-5: FiltroMapperImpl non e un bean CDI

**File:** `layout/mapper/FiltroMapperImpl.java:8`

A differenza di tutti gli altri mapper, non e annotata con `@ApplicationScoped`.

---

### ARCH-6: Date salvate come stringhe

Tutti i campi data sono `String` invece di `java.time.LocalDate` o `LocalDateTime`:
- `Transaction.java:19`
- `TransactionDTO.java:23`
- `Layout.java:19`
- `LayoutDTO.java:23`

Impedisce l'indexing MongoDB e rende le range query inaffidabili.

---

### ARCH-7: Category embedded in Transaction

**File:** `Transaction.java:18`

```java
public Category category;
```

La `Category` e embedded come sub-document in ogni `Transaction`. Se una categoria viene aggiornata, tutte le transazioni esistenti mantengono i dati vecchi (inconsistenza dati).

---

### ARCH-8: Business logic nel repository

**File:** `TransactionRepository.java:18-108`

`ricercaTransaction()` contiene:
- Validazione date (righe 28-31)
- Logica paginazione default (righe 76-84)
- Calcolo metadati paginazione (righe 98-99)
- Assemblaggio oggetto risposta (righe 101-103)

Questa logica appartiene al service layer.

---

## Backend - Sicurezza

### SEC-1: Regex injection (vedi BUG-10)

Input utente interpolato direttamente in pattern regex MongoDB.

---

### SEC-2: Nessuna autenticazione o autorizzazione

Nessun meccanismo di sicurezza (`quarkus-oidc`, `quarkus-elytron-security`, etc.). Tutti gli endpoint sono pubblicamente accessibili.

---

### SEC-3: Connection string MongoDB hardcoded

**File:** `application.yml:31`

```yaml
quarkus:
  mongodb:
    connection-string: mongodb://localhost:27017
```

Se MongoDB richiedesse autenticazione, le credenziali sarebbero in chiaro.

---

### SEC-4: Swagger UI abilitato in produzione

**File:** `application.yml:33-35`

```yaml
quarkus:
  swagger-ui:
    always-include: true
    enable: true
```

Espone lo schema API completo in produzione.

---

### SEC-5: Nessun rate limiting o limite dimensione richieste

Nessuna configurazione per limitare il body delle richieste o il rate delle chiamate.

---

## Backend - Performance

### PERF-1: Nessun indice MongoDB definito

Campi usati nelle query senza indice:
- `Category.codice` - usato in `findByCodice()`
- `Transaction.title` - usato in ricerche regex
- `Transaction.date` - usato in range query
- `Transaction.category.descrizione` / `category.codice` - usato in query su sub-document
- `Layout.layoutName` - usato in `findByLayoutName()`
- `Layout.isDefault` - usato nei filtri

Tutte le query eseguono full collection scan.

---

### PERF-2: Due round-trip al DB per ogni ricerca

**File:** `TransactionRepository.java:96-98`

```java
panacheQuery.page(paginazioneDTO.getNumeroPagina(), paginazioneDTO.getNumeroElementiPerPagina());
paginazioneDTO.setNumeroRisTotali(panacheQuery.count());  // Query separata
```

---

### PERF-3: Regex con leading wildcard non possono usare indici

```java
params.and("title", "(?i).*" + title + ".*");
```

Pattern `.*xxx.*` forzano un full collection scan. Considerare MongoDB text index o Atlas Search.

---

### PERF-4: Entity caricata interamente per le operazioni di delete

**File:** `TransactionsServiceImpl.java:89-92`

```java
Optional<Transaction> transaction = transactionRepository.findByIdOptional(new ObjectId(id));
if(transaction.isPresent()){
    transactionRepository.delete(transaction.get());
```

Usare `deleteById()` sarebbe piu efficiente.

---

### PERF-5: TransactionsServiceImpl @RequestScoped

Come da ARCH-4, `@Model` crea un'istanza per request. Nessuno stato request-specific e presente nel service.

---

## Backend - Testing

### TEST-1: Solo test boilerplate - testa endpoint inesistente

**File:** `src/test/java/org/acme/GreetingResourceTest.java`

```java
@Test
void testHelloEndpoint() {
    given()
      .when().get("/hello")
      .then()
         .statusCode(200)
         .body(is("Hello from Quarkus REST"));
}
```

Non esiste un endpoint `/hello` nell'applicazione. **Zero test** per:
- `CategoryApi`, `LayoutApi`, `TransactionsApi`
- `CategoryServiceImpl`, `LayoutServiceImpl`, `TransactionsServiceImpl`
- Nessun mapper testato
- Nessun integration test con MongoDB (Testcontainers/DevServices)

---

### TEST-2: Dipendenze test mancanti

`pom.xml` non include dipendenze per testing MongoDB (embedded mongo, testcontainers).

---

## Backend - Configurazione

### CONFIG-1: CORS configurato in due file diversi

CORS e presente sia in `application.properties` (righe 1-6) che in `application.yml` (righe 13-20) con valori leggermente diversi. Crea ambiguita su quali valori siano effettivamente attivi.

---

### CONFIG-2: Database name duplicato

**File:** `application.yml:3-5`

```yaml
"%prod":
  quarkus:
    mongodb:
      connection-string: mongodb://mongo:27017/expense-pulse
      database: expense-pulse
```

Il nome del database appare sia nella connection string che nella property `database`.

---

### CONFIG-3: Nessun profilo test per MongoDB

Nessun profilo `%test` e nessuna configurazione DevServices. I test richiedono un'istanza MongoDB manuale.

---

### CONFIG-4: `@CommonsLog` senza dipendenza esplicita

Multipli file usano `@CommonsLog` di Lombok senza una dipendenza esplicita `commons-logging`. Usare `@Slf4j` sarebbe piu appropriato con Quarkus.

---

## Backend - API Design

### API-1: POST usato per operazioni di lettura

- `POST /layouts/default` - recuperare il layout default dovrebbe essere `GET`
- `POST /layouts/reset` - reset al default dovrebbe essere `PUT` o `GET`
- `POST /transactions/ricerca` - la ricerca con filtri puo giustificare il POST per il body, ma e discutibile

---

### API-2: Endpoint duplicati

**File:** `LayoutApi.java:47-52`

`GET /layouts/` e `GET /layouts/all` ritornano gli stessi dati. Ridondante.

---

### API-3: Status code inconsistenti nella documentazione

- `CategoryApi.creaCategory()` ritorna 201 ma la documentazione OpenAPI dice `responseCode = "200"`
- `CategoryApi.aggiornaCategory()` ritorna 204 ma la documentazione dice `responseCode = "200"`

---

### API-4: Nessuna struttura errore standardizzata

Nessun formato errore standard (es. `{"error": "message", "code": "XXX"}`). I client ricevono errori 500 generici.

---

### API-5: Endpoint delete senza documentazione `@APIResponses`

**File:** `TransactionsApi.java:102-115` - manca l'annotazione `@APIResponses`.

---

---

# FRONTEND (Vue 3 + TypeScript)

## Frontend - Bug Critici

### BUG-01: `saveTransaction()` crea una categoria invece di aggiornare una transazione

**File:** `src/components/TransactionHistory.vue:78-127`

La funzione e nominata `saveTransaction()` ma chiama `categoryStore.addCategory(...)` (riga 100) e mostra "Categoria creata con successo" (riga 121). Copy-paste errato dalla logica di creazione categoria.

---

### BUG-03: Spreading di una stringa corrompe lo stato del layout

**File:** `src/constants/app.constants.ts:5` + `src/components/EditControl.vue:23`

```typescript
// app.constants.ts
export const DEFAULT_LAYOUT_HOME = "DEFAULT_LAYOUT_HOME";  // E' una stringa!

// EditControl.vue
layout.value = [...DEFAULT_LAYOUT_HOME];  // Produce ['D','E','F','A','U','L','T','_',...]
```

---

## Frontend - Bug Importanti

### BUG-02: Import eager vanifica il lazy loading

**File:** `src/router/index.ts:3,17`

```typescript
import CategoriesView from '../views/CategoriesView.vue'  // Riga 3: eager (inutilizzato)
// ...
component: () => import('../views/CategoriesView.vue')     // Riga 17: lazy (vanificato)
```

L'import statico a riga 3 carica il modulo subito, rendendo inutile l'import dinamico a riga 17.

---

### BUG-04: Store layout condiviso causa collisione tra view

**File:** `src/stores/layoutStore.ts`

`HomeView` e `CategoriesView` condividono lo stesso singleton `layoutStore` e scrivono su `currentLayout`. Navigando tra le view, il layout di una sovrascrive quello dell'altra.

---

### BUG-05: `window.scrollTo` fuori da onMounted

**File:** `src/views/HomeView.vue:69`

`window.scrollTo({ top: 0, behavior: "smooth" })` eseguito a livello di modulo (fuori da `onMounted`). Duplicato e problematico in ambienti non-browser (SSR/test).

---

### BUG-06: `editTransaction` ref non inizializzata

**File:** `src/components/TransactionHistory.vue:21`

`editTransaction` dichiarata come `ref<TransactionVO>()` senza valore iniziale. Il modal commentato (righe 201-213) usa `editTransaction!.title` con non-null assertion - causerebbe runtime error se riabilitato.

---

### BUG-07: `fetchCategories()` senza await

**File:** `src/views/CategoriesView.vue:54`

```typescript
categoryStore.fetchCategories()  // Manca await - errori inghiottiti silenziosamente
```

---

### BUG-08: `FiltroTransactionDTO.transactions` tipizzato come `any[]`

**File:** `src/models/dtos/FiltroTransactionDTO.ts:4`

Bypassa completamente la type safety di TypeScript. Il DTO sembra anche inutilizzato.

---

## Frontend - Qualità del Codice

### CQ-01: Codice commentato (~200+ righe)

| File | Righe | Contenuto |
|------|-------|-----------|
| `views/HomeView.vue` | 77-143 | ~67 righe di codice morto |
| `views/CategoriesView.vue` | 61-99 | ~39 righe di codice morto |
| `constants/app.constants.ts` | 6-22 | Array commentati |
| `services/layoutService.ts` | 5-23, 70-73 | Logica JWT/reset |
| `stores/layoutStore.ts` | 28-41 | Funzione commentata |
| `components/TransactionHistory.vue` | 188-229 | Intero modal |

---

### CQ-02: `formatCurrency` duplicata in 4 componenti

```typescript
const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(amount)
}
```

**File:**
- `components/BalanceCards.vue:8`
- `components/TransactionHistory.vue:36`
- `components/CategoryStats.vue:26`
- `components/CategoryTable.vue:21`

**Fix:** estrarre in `src/utils/formatters.ts`.

---

### CQ-03: `generateRandomColor` duplicata in 3 componenti

**File:**
- `components/TransactionForm.vue:37-44`
- `components/TransactionHistory.vue:26-33`
- `components/CategoryForm.vue:32-39`

---

### CQ-04: Import inutilizzati

| File | Import |
|------|--------|
| `TransactionHistory.vue:7` | `X` da `lucide-vue-next` |
| `CategoriesView.vue:3` | `type LayoutItemVO` |
| `EditControl.vue:10` | `type LayoutItem` da `chart.js` (fonte sbagliata!) |
| `constants/app.constants.ts:1` | `type LayoutItemVO` |
| `router/index.ts:3` | `CategoriesView` (import eager inutilizzato) |

---

### CQ-05: Emit inutilizzati

| File | Emit dichiarati ma mai chiamati |
|------|-------------------------------|
| `TransactionHistory.vue:45` | `emit('edit', ...)`, `emit('delete', ...)` |
| `TransactionForm.vue:34` | `emit('success')`, `emit('cancel')`, `emit('edit')` |

---

### CQ-06: Typo "vategory" nel emit di CategoryTable

**File:** `src/components/CategoryTable.vue:17`

```typescript
(e: "edit", vategory: CategoryVO): void  // Dovrebbe essere "category"
```

---

### CQ-07: Log prefisso errato

**File:** `src/views/HomeView.vue:169`

Logga `[CategoriesView.handleLayoutChange]` pur essendo in `HomeView`. Artefatto da copy-paste.

---

### CQ-08: Console.log con emoji in tutto il codice

`console.log`/`console.warn`/`console.error` con prefissi emoji sparsi in:
- `categoryStore.ts` (righe 9, 17, 33, 77, 93, 106)
- `expenseStore.ts` (righe 12, 74, 85, 109, 119, 139, 144)
- `layoutStore.ts` (righe 111, 115, 129, 131, etc.)

Dovrebbero essere rimossi o sostituiti con un sistema di logging.

---

### CQ-09: `EditControl.vue` completamente inutilizzato

Non importato ne usato da nessun altro file. Contiene anche BUG-03 (`DEFAULT_LAYOUT_HOME` spreading).

---

## Frontend - Architettura

### ARCH-01: `GridContainer.vue` senza TypeScript

**File:** `src/components/GridContainer.vue:1`

`<script setup>` manca `lang="ts"`. Props definiti con runtime type checks invece di TypeScript generics.

---

### ARCH-02: Mutazione diretta dello store

**File:** `views/HomeView.vue:161-167`, `views/CategoriesView.vue:129-135`

```typescript
layoutStore.currentLayout = { ... }  // Mutazione diretta invece di action dedicata
```

---

### ARCH-03: Service layer completamente inutilizzato

`categoryService.ts` e `layoutService.ts` definiscono metodi (`getAll()`, `create()`, etc.) ma gli store chiamano direttamente `api.get`/`api.post`/`api.put`/`api.delete`. I file service sono codice morto.

---

### ARCH-04: Duplicazione massiva tra HomeView e CategoriesView

Codice quasi identico in entrambe le view:
- Toggle edit mode (`toggleEditMode`)
- Layout change handlers (`handleLayoutChange`)
- Reset layout (`resetLayout`)
- Template GridContainer con slot
- Button bar con icone Lock/Edit3/RotateCcw
- CSS per placeholder e resizing

**Fix:** estrarre in un composable `useLayoutEditor` o in un componente higher-level.

---

### ARCH-05: Nessun error boundary globale

Nessun error handler o fallback. Se un componente lancia un errore durante il render, l'intera app crasha.

---

### ARCH-06: `FiltroTransactionDTO.ts` inutilizzato

Non importato ne usato. Probabilmente sostituito da `FiltroRicercaTransactionDTO.ts`.

---

## Frontend - Sicurezza

### SEC-01: URL API hardcoded

**File:** `src/services/api.ts:4`

```typescript
const api = axios.create({
  baseURL: 'http://localhost:9091/api/v1'
})
```

Nessun `.env` file. Dovrebbe usare `import.meta.env.VITE_API_BASE_URL`.

---

### SEC-02: Protocollo HTTP invece di HTTPS

L'URL usa `http://`. Per produzione serve HTTPS.

---

### SEC-03: Nessuna sanitizzazione input

- `TransactionForm.vue:218` - `title` inviato direttamente all'API senza validazione lunghezza
- `CategoryForm.vue:101` - `descrizione` con solo `.trim()`
- Nessun vincolo di lunghezza massima su nessun input

---

### SEC-04: Link a conversazione Gemini nel codice

**File:** `src/views/CategoriesView.vue:14`

Commento contiene link a conversazione esterna: `https://gemini.google.com/gem/...`. Non dovrebbe essere nel codice sorgente.

---

## Frontend - Performance

### PERF-01: Import eager vanifica lazy loading (vedi BUG-02)

---

### PERF-02: Fetch sequenziali in onMounted

**File:** `src/views/HomeView.vue:51-66`

Layout, transazioni e categorie fetchate sequenzialmente. Potrebbero essere parallele:

```typescript
await Promise.all([
  store.fetchTransactions(),
  categoryStore.fetchCategories()
]);
```

---

### PERF-03: Nessun debounce sulla ricerca

**File:** `src/components/ResearchTable.vue:82`

`@input="applyFilters"` su ogni keystroke genera una chiamata API per ogni carattere digitato.

**Fix:** aggiungere debounce di 300ms.

---

### PERF-04: Intera lista transazioni ri-fetchata dopo ogni operazione

**File:** `src/stores/expenseStore.ts:107,117,131`

Dopo `addTransaction`, `deleteTransaction` e `updateTransaction`, lo store ri-fetcha l'intera lista. Update ottimistici migliorerebbero l'UX.

---

### PERF-05: `vueDevTools()` incluso senza guardia ambiente

**File:** `vite.config.ts:11`

`vueDevTools()` incluso incondizionatamente anche nei build di produzione.

---

### PERF-06: Dockerfile esegue dev server

**File:** `Dockerfile`

Esegue `npm run dev -- --host` invece di buildare e servire i file statici con nginx.

---

## Frontend - TypeScript

### TS-01: Tipo `any` in LayoutItemMapper

**File:** `src/models/mappers/LayoutItemMapper.ts:11,29`

```typescript
toVO(dto: any)                    // Dovrebbe essere LayoutItemDTO
toDTO(vo: LayoutItemVO): any      // Dovrebbe essere LayoutItemDTO
```

---

### TS-02: GridContainer castato a `any`

**File:** `views/HomeView.vue:34`, `views/CategoriesView.vue:22`

```typescript
const GridContainer = _GridContainer as any;  // Disabilita completamente il type checking
```

---

### TS-03: `GridContainer.vue` senza `lang="ts"` (vedi ARCH-01)

---

### TS-04: `env.d.ts` usa `any`

**File:** `env.d.ts:5`

```typescript
const component: DefineComponent<{}, {}, any>  // Indebolisce la type inference per tutti i .vue import
```

---

### TS-05: Catch blocks usano `: any`

File coinvolti: `TransactionForm.vue:178`, `CategoryForm.vue:71`, `TransactionHistory.vue:122`, `CategoryTable.vue:34`

```typescript
catch (e: any)  // Dovrebbe essere `unknown` con type guard
```

---

### TS-06: Slot typing con `any`

**File:** `views/HomeView.vue:223`, `views/CategoriesView.vue:186`

```html
<template #default="{ item }: any">  <!-- Perde la type safety -->
```

---

## Frontend - Integrazione API

### API-01: Nessun interceptor centralizzato

**File:** `src/services/api.ts`

L'istanza axios non ha response interceptor per errori comuni (401, 403, 500, errori di rete). Ogni store duplica il proprio `try/catch`.

---

### API-02: Nessun timeout configurato

L'istanza axios non ha proprieta `timeout`. Richieste a backend lento restano appese indefinitamente.

---

### API-03: Gestione errori inconsistente negli store

| Store | Metodo | Comportamento |
|-------|--------|---------------|
| `expenseStore` | `fetchTransactions` | Inghiotte l'errore |
| `expenseStore` | `addTransaction` | Rilancia l'errore |
| `categoryStore` | `fetchCategories` | Inghiotte l'errore |
| `categoryStore` | `deleteCategory` | Inghiotte l'errore |
| `categoryStore` | `addCategory` | Rilancia l'errore |
| `categoryStore` | `updateCategory` | Rilancia l'errore |

---

## Frontend - UI/UX

### UX-01: Nessun loading indicator

Gli state `loading` esistono negli store (`expenseStore.loading`, `categoryStore.loading`, `layoutStore.loading`) ma nessun componente li usa per mostrare spinner o skeleton.

---

### UX-02: Nessun empty state per le categorie

**File:** `src/components/CategoryTable.vue`

La tabella non mostra un messaggio quando la lista categorie e vuota.

---

### UX-03: Nessun attributo di accessibilita (ARIA)

- Nessun `aria-label` sui pulsanti con sole icone (edit/delete)
- Nessun `aria-current` sui link di navigazione attivi
- Label dei form non associate agli input via `for`/`id`
- `<html lang="">` in `index.html:2` e vuoto

---

### UX-04: Titolo pagina "Vite App"

**File:** `index.html:8`

Il titolo e il valore default dello scaffold. Dovrebbe essere "ExpensePulse".

---

### UX-05: `window.confirm()` e `window.alert()` per azioni critiche

Usati in: `TransactionHistory.vue:67`, `CategoryTable.vue:30`, `TransactionForm.vue:137/145/155/177`, `HomeView.vue:121`, `CategoriesView.vue:116`

Bloccano il thread principale e possono essere bloccati dai browser. Preferire dialog custom o toast con undo.

---

### UX-06: Campo data mancante nel form transazioni

**File:** `src/components/TransactionForm.vue`

Il model include un campo `date` (riga 29) che default a oggi, ma non c'e un `<input type="date">` nel template. La data e sempre quella corrente.

---

### UX-07: Paginazione dentro l'area scrollabile

**File:** `src/components/TransactionHistory.vue:185`

`AppPagination` e renderizzato dentro il container `overflow-y-auto` (riga 138). L'utente deve scrollare fino in fondo per vedere i controlli di paginazione.

---

---

# Raccomandazioni Prioritarie

## Priorita 1 - Bug Critici

1. Fixare `TransactionResponseMapperImpl.convertDtoToEntity()` - ritorna sempre null
2. Fixare `CategoryServiceImpl.aggiornaCategory()` - lookup per codice invece che per id
3. Fixare il catch silenzioso di `MongoWriteException` non-11000
4. Sanitizzare input nelle regex di ricerca con `Pattern.quote()`
5. Fixare `saveTransaction()` nel frontend - crea categoria invece di aggiornare transazione
6. Fixare `DEFAULT_LAYOUT_HOME` - e una stringa, non un array

## Priorita 2 - Architettura e Sicurezza

7. Aggiungere `ExceptionMapper` JAX-RS per risposte errore strutturate
8. Aggiungere Bean Validation (`@Valid`, `@NotBlank`, `@NotNull`) sui DTO
9. Usare variabili d'ambiente per URL API nel frontend
10. Fixare exception handling per preservare gli stack trace
11. Cambiare `@Model` a `@ApplicationScoped` su `TransactionsServiceImpl`
12. Aggiungere indici MongoDB per i campi usati nelle query

## Priorita 3 - Qualita e Manutenibilita

13. Scrivere test reali (unit + integration con DevServices/Testcontainers)
14. Rimuovere tutto il codice commentato
15. Estrarre utility condivise (`formatCurrency`, `generateRandomColor`)
16. Creare composable `useLayoutEditor` per eliminare duplicazione tra view
17. Consolidare configurazione CORS in un solo file
18. Aggiungere debounce sulla ricerca nel frontend
19. Aggiungere interceptor axios centralizzati con timeout

## Priorita 4 - Polish

20. Usare tipi data appropriati invece di stringhe
21. Fixare naming inconsistente (italiano/inglese)
22. Rimuovere import e variabili inutilizzati
23. Migliorare accessibilita (ARIA labels, `lang` attribute)
24. Aggiungere loading indicators e empty states
25. Fixare Dockerfile frontend per build di produzione
