/*
	Lens by HTML5 UP
	html5up.net | @n33co
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
*/

var main = (function($) { var _ = {

	/**
	 * Settings.
	 * @var {object}
	 */
	settings: {

		// Preload all images.
			preload: false,

		// Slide duration (must match "duration.slide" in _vars.scss).
			slideDuration: 500,

		// Layout duration (must match "duration.layout" in _vars.scss).
			layoutDuration: 750,

		// Thumbnails per "row" (must match "misc.thumbnails-per-row" in _vars.scss).
			thumbnailsPerRow: 2,

		// Side of main wrapper (must match "misc.main-side" in _vars.scss).
			mainSide: 'right'

	},

	/**
	 * Window.
	 * @var {jQuery}
	 */
	$window: null,

	/**
	 * Body.
	 * @var {jQuery}
	 */
	$body: null,

	/**
	 * Main wrapper.
	 * @var {jQuery}
	 */
	$main: null,

	/**
	 * Thumbnails.
	 * @var {jQuery}
	 */
	$thumbnails: null,

	/**
	 * Viewer.
	 * @var {jQuery}
	 */
	$viewer: null,

	/**
	 * Toggle.
	 * @var {jQuery}
	 */
	$toggle: null,

	/**
	 * Nav (next).
	 * @var {jQuery}
	 */
	$navNext: null,

	/**
	 * Nav (previous).
	 * @var {jQuery}
	 */
	$navPrevious: null,

	/**
	 * Slides.
	 * @var {array}
	 */
	slides: [],

	/**
	 * Current slide index.
	 * @var {integer}
	 */
	current: null,

	/**
	 * Lock state.
	 * @var {bool}
	 */
	locked: false,

	/**
	 * Keyboard shortcuts.
	 * @var {object}
	 */
	keys: {

		// Escape: Toggle main wrapper.
			27: function() {
				_.toggle();
			},

		// Up: Move up.
			38: function() {
				_.up();
			},

		// Down: Move down.
			40: function() {
				_.down();
			},

		// Space: Next.
			32: function() {
				_.next();
			},

		// Right Arrow: Next.
			39: function() {
				_.next();
			},

		// Left Arrow: Previous.
			37: function() {
				_.previous();
			}

	},

	/**
	 * Initialize properties.
	 */
	initProperties: function() {

		// Window, body.
			_.$window = $(window);
			_.$body = $('body');

		// Thumbnails.
			_.$thumbnails = $('#thumbnails');

		// Viewer.
			_.$viewer = $(
				'<div id="viewer">' +
					'<div class="inner">' +
						'<div class="nav-next"></div>' +
						'<div class="nav-previous"></div>' +
						'<div class="toggle"></div>' +
					'</div>' +
				'</div>'
			).appendTo(_.$body);

		// Nav.
			_.$navNext = _.$viewer.find('.nav-next');
			_.$navPrevious = _.$viewer.find('.nav-previous');

		// Main wrapper.
			_.$main = $('#main');

		// Toggle.
			$('<div class="toggle"></div>')
				.appendTo(_.$main);

			_.$toggle = $('.toggle');

		// IE<9: Fix viewer width (no calc support).
			if (skel.vars.IEVersion < 9)
				_.$window
					.on('resize', function() {
						window.setTimeout(function() {
							_.$viewer.css('width', _.$window.width() - _.$main.width());
						}, 100);
					})
					.trigger('resize');

	},

	/**
	 * Initialize events.
	 */
	initEvents: function() {

		// Window.

			// Remove is-loading-* classes on load.
				_.$window.on('load', function() {

					_.$body.removeClass('is-loading-0');

					window.setTimeout(function() {
						_.$body.removeClass('is-loading-1');
					}, 100);

					window.setTimeout(function() {
						_.$body.removeClass('is-loading-2');
					}, 100 + Math.max(_.settings.layoutDuration - 150, 0));

				});

			// Disable animations/transitions on resize.
				var resizeTimeout;

				_.$window.on('resize', function() {

					_.$body.addClass('is-loading-0');
					window.clearTimeout(resizeTimeout);

					resizeTimeout = window.setTimeout(function() {
						_.$body.removeClass('is-loading-0');
					}, 100);

				});

		// Viewer.

			// Hide main wrapper on tap (<= medium only).
				_.$viewer.on('touchend', function() {

					if (skel.breakpoint('medium').active)
						_.hide();

				});

			// Touch gestures.
				_.$viewer
					.on('touchstart', function(event) {

						// Record start position.
							_.$viewer.touchPosX = event.originalEvent.touches[0].pageX;
							_.$viewer.touchPosY = event.originalEvent.touches[0].pageY;

					})
					.on('touchmove', function(event) {

						// No start position recorded? Bail.
							if (_.$viewer.touchPosX === null
							||	_.$viewer.touchPosY === null)
								return;

						// Calculate stuff.
							var	diffX = _.$viewer.touchPosX - event.originalEvent.touches[0].pageX,
								diffY = _.$viewer.touchPosY - event.originalEvent.touches[0].pageY;
								boundary = 20,
								delta = 50;

						// Swipe left (next).
							if ( (diffY < boundary && diffY > (-1 * boundary)) && (diffX > delta) )
								_.next();

						// Swipe right (previous).
							else if ( (diffY < boundary && diffY > (-1 * boundary)) && (diffX < (-1 * delta)) )
								_.previous();

						// Overscroll fix.
							var	th = _.$viewer.outerHeight(),
								ts = (_.$viewer.get(0).scrollHeight - _.$viewer.scrollTop());

							if ((_.$viewer.scrollTop() <= 0 && diffY < 0)
							|| (ts > (th - 2) && ts < (th + 2) && diffY > 0)) {

								event.preventDefault();
								event.stopPropagation();

							}

					});

		// Main.

			// Touch gestures.
				_.$main
					.on('touchstart', function(event) {

						// Bail on xsmall.
							if (skel.breakpoint('xsmall').active)
								return;

						// Record start position.
							_.$main.touchPosX = event.originalEvent.touches[0].pageX;
							_.$main.touchPosY = event.originalEvent.touches[0].pageY;

					})
					.on('touchmove', function(event) {

						// Bail on xsmall.
							if (skel.breakpoint('xsmall').active)
								return;

						// No start position recorded? Bail.
							if (_.$main.touchPosX === null
							||	_.$main.touchPosY === null)
								return;

						// Calculate stuff.
							var	diffX = _.$main.touchPosX - event.originalEvent.touches[0].pageX,
								diffY = _.$main.touchPosY - event.originalEvent.touches[0].pageY;
								boundary = 20,
								delta = 50,
								result = false;

						// Swipe to close.
							switch (_.settings.mainSide) {

								case 'left':
									result = (diffY < boundary && diffY > (-1 * boundary)) && (diffX > delta);
									break;

								case 'right':
									result = (diffY < boundary && diffY > (-1 * boundary)) && (diffX < (-1 * delta));
									break;

								default:
									break;

							}

							if (result)
								_.hide();

						// Overscroll fix.
							var	th = _.$main.outerHeight(),
								ts = (_.$main.get(0).scrollHeight - _.$main.scrollTop());

							if ((_.$main.scrollTop() <= 0 && diffY < 0)
							|| (ts > (th - 2) && ts < (th + 2) && diffY > 0)) {

								event.preventDefault();
								event.stopPropagation();

							}

					});
		// Toggle.
			_.$toggle.on('click', function() {
				_.toggle();
			});

			// Prevent event from bubbling up to "hide event on tap" event.
				_.$toggle.on('touchend', function(event) {
					event.stopPropagation();
				});

		// Nav.
			_.$navNext.on('click', function() {
				_.next();
			});

			_.$navPrevious.on('click', function() {
				_.previous();
			});

		// Keyboard shortcuts.

			// Ignore shortcuts within form elements.
				_.$body.on('keydown', 'input,select,textarea', function(event) {
					event.stopPropagation();
				});

			_.$window.on('keydown', function(event) {

				// Ignore if xsmall is active.
					if (skel.breakpoint('xsmall').active)
						return;

				// Check keycode.
					if (event.keyCode in _.keys) {

						// Stop other events.
							event.stopPropagation();
							event.preventDefault();

						// Call shortcut.
							(_.keys[event.keyCode])();

					}

			});

	},

	/**
	 * Initialize viewer.
	 */
	initViewer: function() {

		// Bind thumbnail click event.
			_.$thumbnails
				.on('click', '.thumbnail', function(event) {

					var $this = $(this);

					// Stop other events.
						event.preventDefault();
						event.stopPropagation();

					// Locked? Blur.
						if (_.locked)
							$this.blur();

					// Switch to this thumbnail's slide.
						_.switchTo($this.data('index'));

				});

		// Create slides from thumbnails.
			_.$thumbnails.children()
				.each(function() {

					var	$this = $(this),
						$thumbnail = $this.children('.thumbnail'),
						s;

					// Slide object.
						s = {
							$parent: $this,
							$slide: null,
							$slideImage: null,
							$slideCaption: null,
							url: $thumbnail.attr('href'),
							loaded: false
						};

					// Parent.
						$this.attr('tabIndex', '-1');

					// Slide.

						// Create elements.
	 						//s.$slide = $('<div class="slide"><div class="caption"></div><div class="image"></div></div>');
							s.$slide = $('<div class="slide" id="container">                                \
                                                                          <div class="caption"></div>                                   \
                                                                          <image class="image" id="image"/>                             \
                                                                          <div id="zoom_wrapper">                                       \
                                                                             <div id="zoom_sign">                                       \
                                                                                <p class="zoom_text">放大</p>                           \
                                                                                <p class="zoom_text">縮小</p>                           \
                                                                             </div>                                                     \
                                                                             <div id="zoom"></div>                                      \
                                                                          </div>                                                        \
                                                                      </div>');
							// 


	 					// Image.
 							s.$slideImage = s.$slide.children('.image');

 							// Set background stuff.
	 							s.$slideImage
		 					//		.css('background-image', '')
		 							.css('background-position', ($thumbnail.data('position') || 'center'));

						// Caption.
							s.$slideCaption = s.$slide.find('.caption');

							// Move everything *except* the thumbnail itself to the caption.
								$this.children().not($thumbnail)
									.appendTo(s.$slideCaption);

					// Preload?
						if (_.settings.preload) {

							// Force image to download.
								var $img = $('<img src="' + s.url + '" />');

							// Set slide's background image to it.
								s.$slideImage
									//.css('background-image', 'url(' + s.url + ')');
									.attr('src', s.url);
							// Mark slide as loaded.
								s.$slide.addClass('loaded');
								s.loaded = true;

						}

					// Add to slides array.
						_.slides.push(s);

					// Set thumbnail's index.
						$thumbnail.data('index', _.slides.length - 1);

				});

	},
        init_zoom: function(){
TEST = {
    prevscale: 0.9,
    scale: 1,
    container: $('#container'),
    zoomer: $('#zoom'),
    image: $('#image'),
    running: false,
    currentY: 0,
    targetY: 0,
    oldY: 0,
    maxScrollTop: 0,
    minScrollTop: null,
    direction: null,
    fricton: 1, // higher value for slower deceleration
    vy: 0,
    stepAmt: 10,
    minMovement: 0.05,
    ts: 0.1,
    init: function () {
        var self = this;
        self.container=$('#container');
        self.zoomer=$('#zoom');
        self.image=$('#image');
        self.currentLocation = {
            x: 0,
            y: 0
        };
        self.mouseLocation = {
            x: 0,
            y: 0
        };
        self.currentscale = 1;
        self.centreimage();
        self.initslider();
        self.initmousewheel();
    },
    centreimage: function () {

        var self = this;
        //set Image dimensions to max 90% of container
        var imageHeight = 480,
            imageWidth = 640,
            container = $('#container'),
            contWidth = self.container.width(),
            contHeight = self.container.height();

        var ratio = Math.min(contWidth * 0.9 / imageWidth, contHeight * 0.9 / imageHeight);

        self.image.css({
            'height': imageHeight * ratio + 'px',
                'width': imageWidth * ratio + 'px'
        });


    },
    initslider: function () {
        var self = this;
        self.zoomer.slider({
            orientation: 'vertical',
            min: 1,
            max: 10,
            step: 0.01
        })
            .on('slide slidechange', function (event, ui) {
            self.zoom(ui.value);
        });

    },
    initmousewheel: function () {
        var self = this;
        
        self.container.on('mousewheel', function (e, delta) {
            
            if (!self.running) {
                self.running = true;
                self.animateLoop();
            }
            self.delta = delta;
            self.smoothWheel(delta);
            return false;
        });
        
        self.container.on('mousemove', function (e) {
            var offset = self.image.offset();
            self.mouseLocation.x = (e.pageX - offset.left) / self.currentscale;
            self.mouseLocation.y = (e.pageY - offset.top) / self.currentscale;
            self.zoom(self.currentscale);
        });

    },
    zoom: function (scale) {
        var self = this;
        //self.currentLocation.x += ((self.mouseLocation.x - self.currentLocation.x) / self.currentscale);
        //self.currentLocation.y += ((self.mouseLocation.y - self.currentLocation.y) / self.currentscale);
        
        //above should be:
        //self.currentLocation.x += (self.mouseLocation.x - self.currentLocation.x);
        //self.currentLocation.y += (self.mouseLocation.y - self.currentLocation.y);
        
        //equals to:
        //self.currentLocation.x = self.mouseLocation.x;
        //self.currentLocation.y = self.mouseLocation.y;
        
        //in short:
        //self.currentLocation = self.mouseLocation;
        //console.log(self.currentLocation.x, self.currentLocation.y);
        
        var compat = ['-moz-', '-webkit-', '-o-', '-ms-', ''];
        var newCss = {};
        for (var i = compat.length - 1; i; i--) {
            newCss[compat[i] + 'transform'] = 'scale(' + scale + ')';
            newCss[compat[i] + 'transform-origin'] = self.mouseLocation.x + 'px ' + self.mouseLocation.y + 'px';
        }
        self.image.css(newCss);

        self.currentscale = scale;
    },
    smoothWheel: function (amt) {
        this.targetY += amt;
        this.vy += (this.targetY - this.oldY) * this.stepAmt;
        this.oldY = this.targetY;
    },
    render: function () {

        var self = this;
        if (this.vy < -(this.minMovement) || this.vy > this.minMovement) {


            this.vy *= this.fricton;

            if (Math.abs(this.vy) < 0.1) {
                this.running = false;
                this.vy = 0;

            }
            self.zoomer.slider("value", self.zoomer.slider("value") + (self.vy / 100));

        }
    },
    animateLoop: function () {
        var self = this;

        if (!self.running) return;
        window.requestAnimFrame(function () {
            self.animateLoop();
        });
        self.render();
    }
};

TEST.init();

window.requestAnimFrame = (function () {
    return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame || function (callback) {
        window.setTimeout(callback, 1000 / 60);
    };})();

        },
	/**
	 * Initialize stuff.
	 */
	init: function() {

		// IE<10: Zero out transition delays.
			if (skel.vars.IEVersion < 10) {

				_.settings.slideDuration = 0;
				_.settings.layoutDuration = 0;

			}

		// Skel.
			skel.breakpoints({
				xlarge: '(max-width: 1680px)',
				large: '(max-width: 1280px)',
				medium: '(max-width: 980px)',
				small: '(max-width: 736px)',
				xsmall: '(max-width: 480px)'
			});

		// Everything else.
			_.initProperties();
			_.initViewer();
			_.initEvents();

		// Initial slide.
			window.setTimeout(function() {

				// Show first slide if xsmall isn't active or it just deactivated.
					skel.on('-xsmall !xsmall', function() {

						if (_.current === null)
							_.switchTo(0, true);

					});

			}, 0);

	},

	/**
	 * Switch to a specific slide.
	 * @param {integer} index Index.
	 */
	switchTo: function(index, noHide) {

		// Already at index and xsmall isn't active? Bail.
			if (_.current == index
			&&	!skel.breakpoint('xsmall').active)
				return;

		// Locked? Bail.
			if (_.locked)
				return;

		// Lock.
			_.locked = true;

		// Hide main wrapper if medium is active.
			if (!noHide
			&&	skel.breakpoint('medium').active
			&&	skel.vars.IEVersion > 8)
				_.hide();

		// Get slides.
			var	oldSlide = (_.current !== null ? _.slides[_.current] : null),
				newSlide = _.slides[index];

		// Update current.
			_.current = index;

		// Deactivate old slide (if there is one).
			if (oldSlide) {

				// Thumbnail.
					oldSlide.$parent
						.removeClass('active');

				// Slide.
					oldSlide.$slide.removeClass('active');

			}

		// Activate new slide.

			// Thumbnail.
				newSlide.$parent
					.addClass('active')
					.focus();

			// Slide.
				var f = function() {

					// Old slide exists? Detach it.
						if (oldSlide)
							oldSlide.$slide.detach();

					// Attach new slide.
						newSlide.$slide.appendTo(_.$viewer);
                                                _.init_zoom();

					// New slide not yet loaded?
						if (!newSlide.loaded) {

							window.setTimeout(function() {

								// Mark as loading.
									newSlide.$slide.addClass('loading');

								// Wait for it to load.
									$('<img src="' + newSlide.url + '" />').on('load', function() {
									//window.setTimeout(function() {

										// Set background image.
											newSlide.$slideImage
												//.css('background-image', 'url(' + newSlide.url + ')');
												.attr('src', newSlide.url);
										// Mark as loaded.
											newSlide.loaded = true;
											newSlide.$slide.removeClass('loading');

										// Mark as active.
											newSlide.$slide.addClass('active');

										// Unlock.
											window.setTimeout(function() {
												_.locked = false;
											}, 100);

									//}, 1000);
									});

							}, 100);

						}

					// Otherwise ...
						else {

							window.setTimeout(function() {

								// Mark as active.
									newSlide.$slide.addClass('active');

								// Unlock.
									window.setTimeout(function() {
										_.locked = false;
									}, 100);

							}, 100);

						}

				};

				// No old slide? Switch immediately.
					if (!oldSlide)
						(f)();

				// Otherwise, wait for old slide to disappear first.
					else
						window.setTimeout(f, _.settings.slideDuration);

	},

	/**
	 * Switches to the next slide.
	 */
	next: function() {

		// Calculate new index.
			var i, c = _.current, l = _.slides.length;

			if (c >= l - 1)
				i = 0;
			else
				i = c + 1;

		// Switch.
			_.switchTo(i);

	},

	/**
	 * Switches to the previous slide.
	 */
	previous: function() {

		// Calculate new index.
			var i, c = _.current, l = _.slides.length;

			if (c <= 0)
				i = l - 1;
			else
				i = c - 1;

		// Switch.
			_.switchTo(i);

	},

	/**
	 * Switches to slide "above" current.
	 */
	up: function() {

		// Fullscreen? Bail.
			if (_.$body.hasClass('fullscreen'))
				return;

		// Calculate new index.
			var i, c = _.current, l = _.slides.length, tpr = _.settings.thumbnailsPerRow;

			if (c <= (tpr - 1))
				i = l - (tpr - 1 - c) - 1;
			else
				i = c - tpr;

		// Switch.
			_.switchTo(i);

	},

	/**
	 * Switches to slide "below" current.
	 */
	down: function() {

		// Fullscreen? Bail.
			if (_.$body.hasClass('fullscreen'))
				return;

		// Calculate new index.
			var i, c = _.current, l = _.slides.length, tpr = _.settings.thumbnailsPerRow;

			if (c >= l - tpr)
				i = c - l + tpr;
			else
				i = c + tpr;

		// Switch.
			_.switchTo(i);

	},

	/**
	 * Shows the main wrapper.
	 */
	show: function() {

		// Already visible? Bail.
			if (!_.$body.hasClass('fullscreen'))
				return;

		// Show main wrapper.
			_.$body.removeClass('fullscreen');

		// Focus.
			_.$main.focus();

	},

	/**
	 * Hides the main wrapper.
	 */
	hide: function() {

		// Already hidden? Bail.
			if (_.$body.hasClass('fullscreen'))
				return;

		// Hide main wrapper.
			_.$body.addClass('fullscreen');

		// Blur.
			_.$main.blur();

	},

	/**
	 * Toggles main wrapper.
	 */
	toggle: function() {

		if (_.$body.hasClass('fullscreen'))
			_.show();
		else
			_.hide();

	},

}; return _; })(jQuery); main.init(); 
