function gotopage(){

    if(typeof Android !== "undefined" && Android !== null) {

      museumId = document.getElementById("museum_code_id").value;
      zoneId = document.getElementById("lamp_code_id").value;
      Android.getZones(museumId,zoneId);

        } else {
            alert("Not viewing in webview");
        }

}
