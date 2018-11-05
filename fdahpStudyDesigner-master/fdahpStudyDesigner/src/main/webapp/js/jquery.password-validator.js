/*
 *  jquery-password-validator - v0.0.1
 *  Plugin for live validation of password requirements
 *  http://github.com/IoraHealth/jquery-password-validator
 *
 *  Made by Myke Cameron
 *  Under MIT License
 */
this["JST"] = this["JST"] || {};

this["JST"]["input_wrapper"] = function(obj) {
obj || (obj = {});
var __t, __p = '', __e = _.escape;
with (obj) {
__p += '<div class="jq-password-validator">\n';

}
return __p
};

this["JST"]["length"] = function(obj) {
obj || (obj = {});
var __t, __p = '', __e = _.escape;
with (obj) {
__p += '<div class="jq-password-validator__rule is-valid length">\n\tBe at least <em>' +
((__t = ( length )) == null ? '' : __t) +
' characters</em>\n</div>\n';

}
return __p
};

this["JST"]["popover"] = function(obj) {
obj || (obj = {});
var __t, __p = '', __e = _.escape;
with (obj) {
__p += '<div class="jq-password-validator__popover" style="color:#2d2926;">\n\t<header><b>Your password must:</b></header>\n</div>\n';

}
return __p
};

this["JST"]["row"] = function(obj) {
obj || (obj = {});
var __t, __p = '', __e = _.escape;
with (obj) {
__p += '<div class="jq-password-validator__rule ' +
((__t = ( ruleName )) == null ? '' : __t) +
'">\n\t<svg xmlns="http://www.w3.org/2000/svg" class="jq-password-validator__checkmark" viewBox="0 0 8 8">\n\t  <path d="M6.41 0l-.69.72-2.78 2.78-.81-.78-.72-.72-1.41 1.41.72.72 1.5 1.5.69.72.72-.72 3.5-3.5.72-.72-1.44-1.41z" transform="translate(0 1)" />\n\t</svg>\n\t' +
((__t = ( preface )) == null ? '' : __t) +
'\n\t<em>' +
((__t = ( message )) == null ? '' : __t) +
'</em>\n</div>\n';

}
return __p
};
;(function ( $, window, document, undefined ) {

	"use strict";

		// Plugin setup
		var pluginName = "passwordValidator",
				defaults = {
				length: 12,
				require: ["length", "lower", "upper", "digit"]
		};

		// The actual plugin constructor
		function Plugin ( element, options ) {
				this.element = element;
				this.settings = $.extend( {}, defaults, options );
				this._defaults = defaults;
				this._name = pluginName;
				this.init();
		}

		// Actual plugin code follows:

		// Regular expressions used for validation
		var validators = {
				upper: {
						validate: function ( password ) {
								return password.match(/[A-Z]/) != null;
						},
						message: "upper case letter"
				},
				lower: {
						validate: function ( password ) {
								return password.match(/[a-z]/) != null;
						},
						message: "lower case letter"
				},
				digit: {
						validate: function ( password ) {
								return password.match(/\d/) != null;
						},
						message: "number"
				},
				spacial: {
						validate: function ( password ) {
								return password.match(/[!"#$%&'()*+,-.:;<=>?@[\]^_`{|}~]/) != null;
						},
						message: "<span style='border-bottom: 0px;'>special character<br><span style='font-size:12px;color:#a94442;'>! &quot; # $ %&amp; ' () * + , - . : ; &lt; =&gt; ? @ [ \] ^ _ ` {} | ~</span><span>"
				},
				length: {
						validate: function ( password, settings ) {
								return password.length >= settings.length;
						},
						message: function ( settings ) {
								return settings.length + " to 64 characters";
						},
						preface: "Be at least"
				}
		};

		// Avoid Plugin.prototype conflicts
		$.extend(Plugin.prototype, {
				init: function () {
						this.wrapInput( this.element );
						this.inputWrapper.append( this.buildUi() );
						this.bindBehavior();
				},

				wrapInput: function ( input ) {
						$(input).wrap( JST.input_wrapper() );
						this.inputWrapper = $( ".jq-password-validator" );
						return this.inputWrapper;
				},

				buildUi: function () {
						var ui = $( JST.popover() );
						var _this = this;

						_.each(this.settings.require, function ( requirement ) {
								var message;
								if ( validators[requirement].message instanceof Function ) {
										message = validators[requirement].message( _this.settings );
								} else {
										message = validators[requirement].message;
								}

								var preface = validators[requirement].preface || "Contains a";

								var ruleMarkup = JST.row({
									ruleName: requirement,
									message: message,
									preface: preface
								});

								ui.append( $( ruleMarkup ) );
						});

						this.ui = ui;
						ui.hide();
						return ui;
				},

				bindBehavior: function () {
						var _this = this;
						$( this.element ).on( "focus", function () {
								_this.validate();
								_this.showUi();
						} );
						$( this.element ).on( "blur", function () {
								_this.hideUi();
						} );
						$( this.element ).on( "keyup", function () {
							_this.validate();
						} );
				},

				showUi: function () {
						this.ui.show();
						$( this.element ).parent().removeClass("is-hidden");
						$( this.element ).parent().addClass("is-visible");
				},

				hideUi: function () {
						this.ui.hide();
						$( this.element ).parent().removeClass("is-visible");
						$( this.element ).parent().addClass("is-hidden");
				},

				validate: function () {
						var currentPassword = $(this.element).val();
						var _this = this;
						_.each( this.settings.require, function ( requirement) {
								if ( validators[requirement].validate(currentPassword, _this.settings ) ) {
									_this.markRuleValid(requirement);
								} else {
									_this.markRuleInvalid(requirement);
								}
						});
				},

				markRuleValid: function (ruleName) {
					var row = this.ui.find("." + ruleName);
					row.addClass( "is-valid" );
					row.removeClass( "is-invalid" );
				},

				markRuleInvalid: function (ruleName) {
					var row = this.ui.find("." + ruleName);
					row.removeClass( "is-valid" );
					row.addClass( "is-invalid" );
				}
		});

		// A really lightweight plugin wrapper around the constructor,
		// preventing against multiple instantiations
		$.fn[ pluginName ] = function ( options ) {
				return this.each(function() {
						if ( !$.data( this, "plugin_" + pluginName ) ) {
								$.data( this, "plugin_" + pluginName, new Plugin( this, options ) );
						}
				});
		};

})( jQuery, window, document );
