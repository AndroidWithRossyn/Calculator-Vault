// ++++++++++++++++++++++++++++++++++++++++++++++++
// NOTICE!! THIS JAVASCRIPT IS USED JUST FOR DOCS
// YOU MAY NEED SOME CODE FOR YOUR PROJECT
// NOT INCLUDE THIS FILES AS IS
// ++++++++++++++++++++++++++++++++++++++++++++++++

! function($) {

    $(function() {

        // Initialize custom inputs

        PlanInput.init();

        if ($.fn.selectpicker)
            $('.selectpicker').selectpicker();

        // Disable links in docs
        $('[href^="#"]').click(function(e) {
            e.preventDefault()
        })

        // tooltip demo
        $("[data-toggle=tooltip]").tooltip();

        // popover demo
        $("[data-toggle=popover]")
            .popover()

        // button state demo
        $('#fat-btn')
            .click(function() {
                var btn = $(this)
                btn.button('loading')
                setTimeout(function() {
                    btn.button('reset')
                }, 3000)
            })

        // carousel demo
        $('#myCarousel').carousel();
        $('#myCarouselV').carousel();


        /////////////////////////////
        // GMAP v3
        /////////////////////////////

        $.fn.gMap && $('.gmap').each(function() {
            var d = $(this).data('markers').split(';'),
                m = [];

            for (a in d) {
                m.push({
                    'address': d[a]
                })
            }

            $(this).gMap({
                zoom: $(this).data('zoom') || 16,
                markers: m
            });

        });

        /* Style switcher */
        if ('localStorage' in window && window['localStorage'] !== null) {

            var stKey = 'plan-bs3-new';
            localStorage[stKey] && setSS(localStorage[stKey]);

            $("#css-switcher a").click(function() {
                localStorage[stKey] = $(this).attr('rel');
                setSS(localStorage[stKey]);
                return false;
            });
        }

        function setSS(urls) {
            var stylesheet = $("#plan-theme");
            var stylesheetEx = $("#plan-theme-extra");
            var ss = urls.split(',');

            stylesheet.attr("href", ss[0])
            stylesheetEx.attr("href", ss[1])

        }
        /* End Style switcher */

    })
}(window.jQuery)