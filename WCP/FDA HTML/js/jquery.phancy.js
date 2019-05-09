/*!
 * jQuery Phancy Scroll Plugin 0.2
 * https://github.com/dereli/phancy
 *
 * Copyright 2012-2014 Zubeyr Dereli
 * Licensed under MIT license (http://opensource.org/licenses/mit-license.php).
 *
 * Tested with jQuery 1.8.0
 *
 */
(function( jQuery, undefined ) {

    jQuery.fn.extend({
        customScroll: function() {

            // measure the default scrollbar width.
            var tdv = jQuery( "<div><div></div></div>" )
                .css({
                    position: "absolute",
                    left: -1000,
                    width: 300,
                    overflow: "scroll"
                })
                .appendTo( "body" ),
                twi = tdv.width() - tdv.find( "div" ).width();
            tdv.remove();

            return this.each(function() {

                // decorate the element with a scrollbar
                var that = jQuery( this ),
                    he = that.outerHeight(),
                    wi = that.outerWidth();

                var sbp = that.css("direction") === "rtl" ? "left" : "right",
                    sbf = that.css("direction") === "rtl" ? "right" : "left",
                    scroller = jQuery( "<div>" )
                        .addClass( "phancy-scroller" )
                        .css({
                            overflow: "hidden",
                            position: "relative",
                            height: he,
                            width: wi,
                            marginTop: that.css( "margin-top" ),
                            marginBottom: that.css( "margin-bottom" ),
                            marginLeft: that.css( "margin-left" ),
                            marginRight: that.css( "margin-right" ),
                            float: that.css( "float" ),
                        }),
                    scrollarea = jQuery( "<div>" )
                        .css({
                            overflow: "scroll",
                            position: "relative",
                            height: he + twi,
                            width: wi + twi
                        })
                        .appendTo( scroller ),
                    scrollareax = jQuery( "<div>" )
                        .css({ float: sbf })
                        .appendTo( scrollarea );

                that
                    .css({
                        overflow: "visible",
                        height: "auto",
                        margin: 0,
                        float: ""
                    })
                    .after( scroller )
                    .appendTo( scrollareax );

                var nhe = scrollareax.outerHeight( true ),
                    ratio = Math.min( 1, he / nhe );
                if ( ratio >= 1 ) {
                    return;
                }

                // create scrollbars
                var scrollbar = jQuery( "<div>" )
                        .addClass( "phancy-scrollbar" )
                        .css({ height: he })
                        .css( sbp, 0 )
                        .appendTo( scroller ),

                    scrollbarbutton = jQuery( "<div>" )
                        .addClass( "phancy-scrollbarbutton" )
                        .css({ height: he * ratio })
                        .css( sbp, 0 )
                        .appendTo( scrollbar );
                that.css({ width: "-=" + scrollbar.css("width") });

                // bind events
                scroller.scroll(function() {
                    scroller.scrollLeft( 0 ).scrollTop( 0 ); /* http://stackoverflow.com/q/10036044 */
                });
                scrollarea.scroll(function() {
                    scrollbarbutton.css({
                        top: scrollarea.scrollTop() * ratio,
                        height: he * ratio
                    });
                });

                (function() {
                    var dragging = false,
                        pageY = null,
                        pageX = null,
                        top = null,
                        timer = null;

                    // scroll by clicking on scrollbar itself (page up and down).
                    scrollbar.on( "mousedown", function( e ) {
                        if ( e.which !== 1 || jQuery( e.target ).hasClass( "scrollbarbutton" ) )
                        {
                            return;
                        }
                        top = parseInt( scrollbarbutton.css( "top" ), 10  ) + ( he * ratio * ( e.pageY > scrollbarbutton.offset().top ? 1 : -1 ));
                        clearTimeout( timer );
                        timer = setTimeout(function() {
                            top = Math.min( Math.max( 0, e.pageY - scrollbar.offset().top ) - he * ratio / 2, he - ( he * ratio ) );
                            scrollbarbutton.css({ top: top });
                            scrollarea.scrollTop( Math.round( top / ratio ) );
                        }, 300);
                        scrollbarbutton.css({ top: top });
                        scrollarea.scrollTop( Math.round( top / ratio ) );
                        return false;
                    });

                    scrollbar.on("mouseup", function() {
                        clearTimeout( timer );
                    });

                    // scroll by clicking on scrollbar button (dragging).
                    scrollbarbutton.on("mousedown", function( e ) {
                        if ( e.which !== 1 )
                        {
                            return;
                        }
                        dragging = true;
                        pageY = e.pageY;
                        pageX = e.pageX;
                        top = parseInt( scrollbarbutton.css( "top" ), 10 );
                        jQuery( document ).on( "mousemove", function( e ) {
                            if ( dragging ) {
                                if ( Math.abs( e.pageX - pageX ) < 50 ) {
                                    var newtop = Math.min( Math.max( 0, top + e.pageY - pageY ), he - he * ratio );
                                    scrollbarbutton.css( "top", newtop );
                                    scrollarea.scrollTop( Math.round( newtop / ratio ) );
                                }
                                else {
                                    scrollarea.scrollTop( Math.round( top / ratio ) );
                                    scrollbarbutton.css({ top: top });
                                }
                                return false;
                            }
                            else {
                                jQuery( document ).unbind( "mousemove" );
                            }
                        });
                        return false;
                    });

                    jQuery( document ).on( "mouseup", function() {
                        if ( dragging ) {
                            dragging = false;
                            jQuery( document ).unbind( "mousemove" );
                            return false;
                        }
                    });
                })();
            });
        }
    });

})( jQuery );
