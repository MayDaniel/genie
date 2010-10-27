$(document).ready(function() {
  $("#dialog").dialog({
    modal: true,
    draggable: false,
    resizable: false,
    buttons: {Close: f() {$(this).dialog('close');}}});});
