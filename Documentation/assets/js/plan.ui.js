/////////////////////////////////////////////////
// PLAN JS for UI ELements
// Include this file if are using extended ui elements
// - Prepend/Append input
// - Custom checkbox/radios
// - Accordion icon toggle
// - Toggles
/////////////////////////////////////////////////

! function($) {

    ////////////////////////
    // INPUT APPEND/PREPEND
    ////////////////////////

    window.PlanInput = {


        init: function() {

            // Properties
            this.checkboxIconChecked = "fa fa-check";
            this.radioIconChecked = "fa-circle-o";
            this.radioIconUnchecked = "fa-circle";
            this.igRadioIconChecked = "fa-circle"; // radio inside input-group
            this.accordionIconOpen = 'fa-minus';
            this.accordionIconClose = 'fa-plus';

            // Methods
            this.initInputGroup();
            this.initCheckbox();
            this.initRadio();
            this.initInputGroupCheckbox();
            this.initInputGroupRadio();
            this.initToggle();
            this.initAccordionIconToggle();

        },

        // INPUT GROUPS FOCUS
        // Initializes once.
        initInputGroup: function() {
            $(document)
                .on('focusin', '.input-group input', function() {
                    $(this).parent().addClass('input-focused');
                })
                .on('focusout', '.input-group input', function() {
                    $(this).parent().removeClass('input-focused');
                });
        },

        // CHECKBOX INSIDE INPUT GROUP
        // Requires initialization for dynamic components (i.e. ajax)
        initInputGroupCheckbox: function() {
            var self = this;
            $('.input-group-addon > input[type=checkbox]')
                .not('.pui-init') // a flag to know the input has been initialized
                .each(function() {
                    var checkbox = $(this).addClass('pui-init'),
                        addon = checkbox.parent();

                    addon.addClass('has-checkbox');

                    var overlay = $('<span class="ctrl-overlay"/>').appendTo(addon);

                    if (checkbox[0].checked)
                        if (!checkbox[0].disabled)
                            addon.addClass('checked');

                    overlay.addClass(self.checkboxIconChecked);

                }).parent().on('click', function() {
                    var checkbox = $(this).find('input:checkbox');

                    if (checkbox[0].disabled) return;

                    checkbox[0].checked = !checkbox[0].checked;
                    $(this)[checkbox[0].checked ? 'addClass' : 'removeClass']('checked');
                });
        },

        // RADIO INSIDE INPUT GROUP
        // Requires initialization for dynamic components (i.e. ajax)
        initInputGroupRadio: function() {
            var self = this;
            $('.input-group-addon > input[type=radio]')
                .not('.pui-init') // a flag to know the input has been initialized
                .each(function() {
                    var radio = $(this).addClass('pui-init'),
                        addon = radio.parent();

                    addon.addClass('has-radio');

                    var overlay = $('<span class="ctrl-overlay"/>').appendTo(addon);

                    if (radio[0].checked)
                        if (!radio[0].disabled)
                            addon.addClass('checked');

                    overlay.addClass(self.igRadioIconChecked);

                }).parent().on('click', function() {
                    var $this = $(this),
                        radio = $this.find('input:radio'),
                        overlay = $this.find('.ctrl-overlay');

                    if (radio[0].disabled) return;

                    $('input:radio[name=' + radio.attr('name') + ']')
                        .not(':disabled')
                        .parent().removeClass('checked');

                    radio[0].checked = !radio[0].checked;
                    $this[radio[0].checked ? 'addClass' : 'removeClass']('checked');

                });
        },

        // RADIOS
        // Requires initialization for dynamic components (i.e. ajax)
        initRadio: function() {
            var self = this;
            $('.radio')
                .not('.pui-init') // a flag to know the input has been initialized
                .each(function() {
                    var $this = $(this),
                        radio = $this.find('input:radio'),
                        label = $this.children('label'),
                        overlay = $('<span class="ctrl-overlay"/>').appendTo(label);

                    overlay.addClass('fa-circle');

                    if (radio[0].checked) {
                        if (!radio[0].disabled)
                            $this.addClass('checked');
                        overlay.addClass(self.radioIconChecked).removeClass(self.radioIconUnchecked);
                    }
                }).on('click', function() {
                    var $this = $(this),
                        radio = $this.find('input:radio'),
                        overlay = $this.find('.ctrl-overlay');

                    if (radio[0].disabled) return;

                    $('input:radio[name=' + radio.attr('name') + ']')
                        .not(':disabled')
                        .siblings('.ctrl-overlay').removeClass(self.radioIconChecked).addClass(self.radioIconUnchecked)
                        .parent().parent().removeClass('checked');


                    if (radio[0].checked) {
                        $this.addClass('checked');
                        overlay.removeClass(self.radioIconUnchecked).addClass(self.radioIconChecked);
                    }

                });
        },

        // CHECKBOX
        // Requires initialization for dynamic components (i.e. ajax)
        initCheckbox: function() {
            var self = this;
            $('.checkbox')
                .not('.pui-init') // a flag to know the input has been initialized
                .each(function() {
                    var $this = $(this).addClass('pui-init'),
                        checkbox = $this.find('input:checkbox'),
                        label = $this.children('label'),
                        overlay = $('<span class="ctrl-overlay"/>').appendTo(label);

                    if (checkbox[0].checked) {
                        if (!checkbox[0].disabled)
                            $this.addClass('checked');
                        overlay.addClass(self.checkboxIconChecked);
                    }
                }).on('click', function() {
                    var $this = $(this),
                        input = $this.find('input:checkbox'),
                        overlay = $this.find('.ctrl-overlay');

                    if (input[0].disabled) return;

                    $this[input[0].checked ? 'addClass' : 'removeClass']('checked');
                    overlay[input[0].checked ? 'addClass' : 'removeClass'](self.checkboxIconChecked);

                });
        },

        // TOGGLE
        // Initializes once.
        initToggle: function() {
            $(document)
                .on('click', '.toggle', function(e) {
                    var $this = $(this);
                    if (!$this.hasClass('disabled'))
                        $this
                        .toggleClass('on');
                    $this
                        .find($this.hasClass('on') ? '.toggle-on' : '.toggle-off')
                        .children('input[type="radio"]')
                        .prop('checked', true);

                })
                // Stop bubbleup of radio click to avoid double click
                .on('click', '.toggle input[type="radio"]', function(e) {
                    e.stopPropagation();
                });
        },

        // ACCORDION ICON TOGGLE
        // Initializes once.
        initAccordionIconToggle: function() {
            var self = this;
            $(document).on('show.bs.collapse hide.bs.collapse', '.accordion', function(e) {
                var $target = $(e.target);
                $target.siblings('.accordion-heading')
                    .find('em').toggleClass(self.accordionIconOpen + ' ' + self.accordionIconClose);
                if (e.type == 'show')
                    $target.prev('.accordion-heading').find('.accordion-toggle').addClass('active');
                if (e.type == 'hide')
                    $(this).find('.accordion-toggle').not($target).removeClass('active');
            });
        },

    };


}(window.jQuery);