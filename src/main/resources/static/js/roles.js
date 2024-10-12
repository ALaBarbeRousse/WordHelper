let roles, users;

$(document).ready(function() {
    loadData();
});
function loadData() {
    $.ajax({
        type: "GET",
        url: "api/student/data",
        success: function (data) {
            roles = data.roles;
            for (let i = 0; i < roles.length; i++) {
                $('.user_roles_list').append('<li class="disabled"><input type="checkbox" value="' + roles[i] + '" disabled/>' + roles[i] + '</li>');
            }

            users = new Map();
            for (let i = 0; i < data.students.length; i++) {
                users.set(data.students[i].name, data.students[i].authorities);
                $('#user_list').append($('<option />').val(data.students[i].name).text(data.students[i].name));
            }
            setListener();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            alert("Could not get users data.");
        }
    });
}
function setListener() {
    $('#user').on('input propertychange', function () {
        let selected = $('#user').val();
        if (users.get(selected)) {
            $('.disabled input').prop('disabled', false);
            $('.user_roles_list li').removeClass("disabled");
            $.each(users.get(selected), function (index, value) {
                $('input[value="' + value + '"]').prop('checked', true);
            });
            $('#ok_btn').prop("disabled", false);
        } else {
            $('.user_roles_list li').addClass("disabled");
            $('.disabled').children('input').prop('disabled', true).prop('checked', false);
            $('#ok_btn').prop("disabled", true);
        }
    });

    $('#ok_btn').on('click', function (e) {
        let student = {};
        student.name = $('#user').val();
        student.authorities = [];
        $('.user_roles_list li input:checked').each(function () {
            student.authorities.push($(this).attr('value'));
        });

        $.ajax({
            type: "PUT",
            url: "api/student/data",
            data: student,
            success: function (data) {
                location.href='/main';
            },
            error: function(jqXHR, textStatus, errorThrown) {
                $("#message").text('Не удалось обновить роли пользователя');
                $("#message").fadeIn(10);
                setTimeout(function() {
                    $('#message').fadeOut(3000);
                }, 5000);
            }
        });
    });
}