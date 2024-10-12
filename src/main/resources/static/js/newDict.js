$(document).ready(function() {
    $("#ok_btn").click(function() {
        createDict();
    });
});

function createDict() {
//    TODO собрать данные из всего двух полей и скомпоновать json
    let l1 = $("#left_field").val();
    let l2 = $("#right_field").val();

    jsonObj = {"lang1": l1, "lang2": l2};

    $.ajax({
        type: "POST",
        url: "api/create",
        data: jsonObj,
        success: function (data, textStatus, jqXHR) {
            alert("success");
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("Failure");
        }
    });
}
