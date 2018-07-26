var disqusFunction = function () {
        var disqus_shortname = 'indicatorbinary-ru';

        // ajax request to load the disqus javascript
        $.ajax({
            type: "GET",
            url: "https://" + disqus_shortname + ".disqus.com/embed.js",
            dataType: "script",
            cache: true
        });

};
$("#comment_load").change(function () {
        if (this.checked) {
            $.cookie("comment_load", "1", {expires: 365, path: '/'}); // Sample 1
            $('.show-comments').fadeOut();
            disqusFunction();
        } else {
            $.cookie("comment_load", "0", {expires: 365, path: '/'}); // Sample 1
        }
});
if ($.cookie("comment_load") == "1") {
        setTimeout(function () {
            if (document.querySelector('.show-comments') !== null) {
                $('#comment_load').prop('checked', true);
                $('.show-comments').fadeOut();
                disqusFunction();
            }
        }, 3000);

}
$('.show-comments').on('click', function () {
        disqusFunction();
        // hide the button once comments load
        $(this).fadeOut();
});
