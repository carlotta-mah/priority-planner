$(document).foundation()


/**
 * Diese Funktion sorgt dafür das nur Zahlen erkant werden
 * @param evt Das Event
 */
function validate(evt) {
    var theEvent = evt || window.event;

    if (theEvent.type === 'paste') {
        key = event.clipboardData.getData('text/plain');
    } else {
        // wenn eine Taste gedrückt wird
        var key = theEvent.keyCode || theEvent.which;
        key = String.fromCharCode(key);
    }
    var regex = /^[0-9]*$/;
    // var charCode = (evt.which) ? evt.which : evt.keyCode;
    if( !regex.test(key)) {
    // if(((charCode > 31 && (charCode < 48 || charCode > 57)))){
        theEvent.returnValue = false;
        if(theEvent.preventDefault) theEvent.preventDefault();
    }
}

/**
 * Aktiviert bzw. deaktiviert ein Object. Wenn "none" dann wird es auf "block" gesetzt sonst auf "none"
 * @param open Das Object welches deaktiviert / Aktiviert werden soll
 */
function toggleElement(open) {

    if (open.style.display === "none") {
        open.style.display = "block";
    } else {
        open.style.display = "none";
    }
}
