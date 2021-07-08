// default của giao diện
(function ($) {
    "use strict";

    // Add active state to sidbar nav links
    var path = window.location.href; // because the 'href' property of the DOM element is the absolute path
    $("#layoutSidenav_nav .sb-sidenav a.nav-link").each(function () {
        if (this.href === path) {
            $(this).addClass("active");
        }
    });
    $('#zone').select2({
        theme: 'bootstrap4',
    })
    // Toggle the side navigation
    $("#sidebarToggle").on("click", function (e) {
        e.preventDefault();
        $("body").toggleClass("sb-sidenav-toggled");
    });

    doLogin();
    /**
     * Bổ sung đăng nhập vào hệ thống không cần đủ 
     * đuôi miền của account
     */
    var submitUsername;
    $('#inputEmailAddress').change(function () {
        this.value = this.value.replace(/[^A-Za-z0-9_-_.]/g, '');
        submitUsername = this.value;

        $('#usernameHidden').val(submitUsername);
    });

    $('#inputEmailAddress').on('keyup', function () {
        this.value = this.value.replace(/[^A-Za-z0-9_-_.]/g, '');
        submitUsername = this.value;

        $('#usernameHidden').val(submitUsername);
    });

    var storage = localStorage;
    $('.menuParent').on('click', function () {
        var expand = $(this).attr('aria-expanded');
        var id = $(this).attr('id');
        if (expand != "true") {
            var obj = {id: id, expand: "true"};
            storage.setItem("page", JSON.stringify(obj));
        } else {
            storage.removeItem("page");
        }
    });
    var page = localStorage.getItem("page");
    if (page != null) {
        var obj = JSON.parse(page);
        $("." + obj.id).addClass("show");
        $("#" + obj.id).attr('aria-expanded', obj.expand);
    }
})(jQuery);

/**
 * Đăng nhập người dùng trên hệ thống 
 */

function doLogin() {
    $('#form-login').on('submit', function (e) {
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: 'api/login',
            dataType: 'json',
            data: $('#form-login').serialize(),
            success: function (response) {
                location.reload();
            },
            error: function (error) {
                $('#msgAlert').text(error.responseJSON.msg);
                $('#msgAlert').show();
            }
        });

    })
}



