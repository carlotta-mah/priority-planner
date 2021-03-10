/**
 * Extrahiert die haupt Domain aus der aktuellen Domain
 * @returns {string} Domain der Hauptseite
 */
function getURL() {
    return window.location.protocol + "//" + window.location.host;
}
