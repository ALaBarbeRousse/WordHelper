$(document).ready(function() {
    $("#user_label").on("click", function (){
        alert("Clicked");
    });

    checkUser();
});

function checkUser() {
    if (!$("#dropdown_btn").text()) {
        $(".user_pane").empty();
    }
}