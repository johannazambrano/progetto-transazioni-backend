// Script di migrazione: converte i campi data da stringa BSON a ISODate.
//
// Uso:
//   mongosh "mongodb://localhost:27017/expense-pulse" scripts/migrate-dates-to-isodate.js
//
// Oppure con Docker:
//   docker exec -i <container> mongosh "mongodb://localhost:27017/expense-pulse" < scripts/migrate-dates-to-isodate.js

// ---------- TRANSACTION.date  (String "yyyy-MM-dd" → ISODate) ----------
let txUpdated = 0;
db.TRANSACTION.find({ date: { $type: "string" } }).forEach(doc => {
    const parsed = new Date(doc.date + "T00:00:00.000Z");
    if (isNaN(parsed.getTime())) {
        print("WARN: TRANSACTION " + doc._id + " ha date non parsabile: " + doc.date);
        return;
    }
    db.TRANSACTION.updateOne(
        { _id: doc._id },
        { $set: { date: parsed } }
    );
    txUpdated++;
});
print("TRANSACTION: " + txUpdated + " documenti migrati.");

// ---------- LAYOUT.updatedAt  (String "yyyy-MM-ddTHH:mm:ss" → ISODate) ----------
let lyUpdated = 0;
db.LAYOUT.find({ updatedAt: { $type: "string" } }).forEach(doc => {
    // Supporta sia "yyyy-MM-dd" che "yyyy-MM-ddTHH:mm:ss" e varianti
    let dateStr = doc.updatedAt;
    if (!dateStr.includes("T")) {
        dateStr += "T00:00:00.000Z";
    } else if (!dateStr.endsWith("Z") && !dateStr.includes("+")) {
        dateStr += "Z";
    }
    const parsed = new Date(dateStr);
    if (isNaN(parsed.getTime())) {
        print("WARN: LAYOUT " + doc._id + " ha updatedAt non parsabile: " + doc.updatedAt);
        return;
    }
    db.LAYOUT.updateOne(
        { _id: doc._id },
        { $set: { updatedAt: parsed } }
    );
    lyUpdated++;
});
print("LAYOUT: " + lyUpdated + " documenti migrati.");
