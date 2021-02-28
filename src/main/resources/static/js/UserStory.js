
class UserStory{


     constructor(name, beschreibung, id) {
         this._name = name;
         this._beschreibung = beschreibung;
         this._id = id;
     }

     get id(){
         return this._id;
     }
     set id(id){
         this._id = id;
     }
     get name() {
         return this._name;
     }

     set name(value) {
         this._name = value;
     }

     get beschreibung() {
         return this._beschreibung;
     }

     set beschreibung(value) {
         this._beschreibung = value;
     }

     get value1() {
         return this._value1;
     }

     set value1(value) {
         this._value1 = value;
     }

     get value2() {
         return this._value2;
     }

     set value2(value) {
         this._value2 = value;
     }

     get zeit() {
         return this._zeit;
     }

     set zeit(value) {
         this._zeit = value;
     }

     get string(){
         let string = [this.name, this.beschreibung, this.value1, this.value2, this.zeit];
         //return string.join(", ");
         console.log(string.join(", "));
    }
 }