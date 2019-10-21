/*
 * File:        jquery.dataTables.rowReordering.js
 * Version:     1.2.2 / Datatables 1.10 hack
 * Author:      Jovan Popovic
 *
 * Copyright 2013 Jovan Popovic, all rights reserved.
 *
 * This source file is free software, under either the GPL v2 license or a
 * BSD style license, as supplied with this software.
 *
 * This source file is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * 
 * Modified by Jeremie Legrand (KOMORI-CHAMBON SAS):
 *  - fast and ugly modification for Datatables 1.10 compatibility (at least, move rows even if it's really slow...)
 *  - can prevent datatable to actually DO the reordering at the end (after the success ajax call), for example
 *      if we want to reload the whole table in ajax just after:
 *              oTable.rowReordering({
 *                      sURL: 'UpdateRowOrder.php',
 *                      avoidMovingRows: true
 *              });
 *  - can call a callback() function when drop is finished
 *      (Integration of a free comment in: https://code.google.com/p/jquery-datatables-row-reordering/wiki/Index)
 *              Author: "Comment by ra...@webrun.ca, Mar 16, 2013"
 *  - can pass additional data in POST, like this:
 *              oTable.rowReordering({
 *                       sURL: 'UpdateRowOrder.php',
 *                              sData: {'var_name': 'big content here'}
 *                      });
 * - on Ajax error return code, give to fnError the response text instead of xhr.statusText, if any,
 * - FIX a crash when 'tr' in 'tbody' didn't have an ID (the function fnGetState() made
 *              a $("#" + id, oTable) to get the 'tr' instead of just get it from the context) 
 * -
 *
 * Parameters:
 * @iIndexColumn     int         Position of the indexing column
 * @sURL             String      Server side page tat will be notified that order is changed
 * @iGroupingLevel   int         Defines that grouping is used
 */
(function ($) {

   "use strict";
   $.fn.rowReordering = function (options) {

      function _fnStartProcessingMode(oTable) {
         ///<summary>
         ///Function that starts "Processing" mode i.e. shows "Processing..." dialog while some action is executing(Default function)
         ///</summary>

         if (oTable.fnSettings().oFeatures.bProcessing) {
            $(".dataTables_processing").css('visibility', 'visible');
         }
      }

      function _fnEndProcessingMode(oTable) {
         ///<summary>
         ///Function that ends the "Processing" mode and returns the table in the normal state(Default function)
         ///</summary>

         if (oTable.fnSettings().oFeatures.bProcessing) {
            $(".dataTables_processing").css('visibility', 'hidden');
         }
      }

      ///Not used
      function fnGetStartPosition(oTable, sSelector) {
         var iStart = 1000000;
         $(sSelector, oTable).each(function () {
            iPosition = parseInt(oTable.fnGetData(this, properties.iIndexColumn));
            if (iPosition < iStart)
               iStart = iPosition;
         });
         return iStart;
      }

      function fnCancelSorting(oTable, tbody, properties, iLogLevel, sMessage) {
         tbody.sortable('cancel');
         if(iLogLevel<=properties.iLogLevel){
            if(sMessage!= undefined){
               properties.fnAlert(sMessage, "");
            }else{
               properties.fnAlert("Row cannot be moved", "");
            }
         }
         properties.fnEndProcessingMode(oTable);
      }

      // ### KCM ### Get $('tr') instead of 'tr id', to avoid re-mapping in jQuery object from it's id (which can be null)
      // #function fnGetState(oTable, sSelector, id) {
      function fnGetState(oTable, sSelector, tr) {
         //var tr = $("#" + id, oTable);
      // ### END ###
         var iCurrentPosition = oTable.fnGetData(tr[0], properties.iIndexColumn);
         var iNewPosition = -1; // fnGetStartPosition(sSelector);
         var sDirection;
         var trPrevious = tr.prev(sSelector);
         if (trPrevious.length > 0) {
            iNewPosition = parseInt(oTable.fnGetData(trPrevious[0], properties.iIndexColumn));
            if (iNewPosition < iCurrentPosition) {
               iNewPosition = iNewPosition + 1;
            }
         } else {
            var trNext = tr.next(sSelector);
            if (trNext.length > 0) {
               iNewPosition = parseInt(oTable.fnGetData(trNext[0], properties.iIndexColumn));
               if (iNewPosition > iCurrentPosition)//moved back
               iNewPosition = iNewPosition - 1;
            }
         }
         if (iNewPosition < iCurrentPosition)
            sDirection = "back";
         else
            sDirection = "forward";

         return { sDirection: sDirection, iCurrentPosition: iCurrentPosition, iNewPosition: iNewPosition };

      }

      function fnMoveRows(oTable, sSelector, iCurrentPosition, iNewPosition, sDirection, id, sGroup) {
         var iStart = iCurrentPosition;
         var iEnd = iNewPosition;
         if (sDirection == "back") {
            iStart = iNewPosition;
            iEnd = iCurrentPosition;
         }

         $(oTable.fnGetNodes()).each(function () {
            if (sGroup != "" && $(this).attr("data-group") != sGroup)
               return;
            var tr = this;
            var iRowPosition = parseInt(oTable.fnGetData(tr, properties.iIndexColumn));
            if (iStart <= iRowPosition && iRowPosition <= iEnd) {
               if (tr.id == id) {
                  oTable.fnUpdate(iNewPosition,
                        oTable.fnGetPosition(tr), // get row position in current model
                        properties.iIndexColumn,
                        false); // false = defer redraw until all row updates are done
               } else {
                  if (sDirection == "back") {
                     oTable.fnUpdate(iRowPosition + 1,
                           oTable.fnGetPosition(tr), // get row position in current model
                           properties.iIndexColumn,
                           false); // false = defer redraw until all row updates are done
                  } else {
                     oTable.fnUpdate(iRowPosition - 1,
                           oTable.fnGetPosition(tr), // get row position in current model
                           properties.iIndexColumn,
                           false); // false = defer redraw until all row updates are done
                  }
               }
            }
         });

         var oSettings = oTable.fnSettings();

         //Standing Redraw Extension
         //Author:   Jonathan Hoguet
         //http://datatables.net/plug-ins/api#fnStandingRedraw
         if (oSettings.oFeatures.bServerSide === false) {
            var before = oSettings._iDisplayStart;
            oSettings.oApi._fnReDraw(oSettings);
            //iDisplayStart has been reset to zero - so lets change it back
            oSettings._iDisplayStart = before;
            oSettings.oApi._fnCalculateEnd(oSettings);
         }
         //draw the 'current' page
         oSettings.oApi._fnDraw(oSettings);
      }

      function _fnAlert(message, type) { alert(message); }

      var defaults = {
            iIndexColumn: 0,
            iStartPosition: 1,
            sURL: null,
            sRequestType: "POST",
            iGroupingLevel: 0,
            fnAlert: _fnAlert,
            fnSuccess: jQuery.noop,
            iLogLevel: 1,
            sDataGroupAttribute: "data-group",
            fnStartProcessingMode: _fnStartProcessingMode,
            fnEndProcessingMode: _fnEndProcessingMode,
            fnUpdateAjaxRequest: jQuery.noop
      };

      var properties = $.extend(defaults, options);

      var iFrom, iTo;

      // Return a helper with preserved width of cells (see Issue 9)
      var tableFixHelper = function(e, tr) 
      {
         var $originals = tr.children();
         var $helper = tr.clone();
         $helper.children().each(function(index)
               {
            // Set helper cell sizes to match the original sizes
            $(this).width($originals.eq(index).width())
               });
         return $helper;
      };

      // ### KCM ### Ugly and fast method to get dataTable object
      var tables;
      if(this instanceof jQuery){
         tables = this;
      } else {
         tables = this.context;
      }

      $.each(tables, function () {
         var oTable;

         if (typeof this.nodeType !== 'undefined'){
            oTable = $(this).dataTable();
         } else {
            oTable = $(this.nTable).dataTable();
         }
         
         var aaSortingFixed = (oTable.fnSettings().aaSortingFixed == null ? new Array() : oTable.fnSettings().aaSortingFixed);
         aaSortingFixed.push([properties.iIndexColumn, "asc"]);

         oTable.fnSettings().aaSortingFixed = aaSortingFixed;

         
         for (var i = 0; i < oTable.fnSettings().aoColumns.length; i++) {
            oTable.fnSettings().aoColumns[i].bSortable = false;
            /*for(var j=0; j<aaSortingFixed.length; j++)
         {
         if( i == aaSortingFixed[j][0] )
         settings.aoColumns[i].bSortable = false;
         }*/
         }
         oTable.fnDraw();

         $("tbody", oTable).disableSelection().sortable({
            cursor: "move",
            helper: tableFixHelper,
            update: function (event, ui) {
               var $dataTable = oTable;
               var tbody = $(this);
               var sSelector = "tbody tr";
               var sGroup = "";
               if (properties.bGroupingUsed) {
                  sGroup = $(ui.item).attr(properties.sDataGroupAttribute);
                  if(sGroup==null || sGroup==undefined){
                     fnCancelSorting($dataTable, tbody, properties, 3, "Grouping row cannot be moved");
                     return;
                  }
                  sSelector = "tbody tr[" + properties.sDataGroupAttribute + " ='" + sGroup + "']";
               }

               // ### KCM ###
               //      pass 'tr' directly, instead of giving id then redo a $('#' + id) in the function...
               // #var oState = fnGetState($dataTable, sSelector, ui.item.context.id);
               var tr = $( ui.item.context );
               var oState = fnGetState($dataTable, sSelector, tr);
               /// ### END ###
               if(oState.iNewPosition == -1) {
                  fnCancelSorting($dataTable, tbody, properties,2);
                  return;
               }

               if (properties.sURL != null) {
                  properties.fnStartProcessingMode($dataTable);
                  var oAjaxRequest = {
                        url: properties.sURL,
                        type: properties.sRequestType,
                        data: { id: ui.item.context.id,
                           fromPosition: oState.iCurrentPosition,
                           toPosition: oState.iNewPosition,
                           direction: oState.sDirection,
                           group: sGroup
                           // ### KCM ### Can pass additional data in POST
                           ,data: properties.sData
                           // ### END ###
                        },
                        success: function (data) {
                           properties.fnSuccess(data);

                           // ###KCM### Can avoid moving rows if we want (for example if we reload all the table in ajax juste after)
                           if(! properties.avoidMovingRows)
                              fnMoveRows($dataTable, sSelector, oState.iCurrentPosition, oState.iNewPosition, oState.sDirection, ui.item.context.id, sGroup);
                           // ### END ###
                           properties.fnEndProcessingMode($dataTable);

                           // ###KCM### Can have a callback when drop is finished
                           // Source: 
                           //      https://code.google.com/p/jquery-datatables-row-reordering/wiki/Index,
                           //      --> Free comment of "Comment by ra...@webrun.ca, Mar 16, 2013"
                           if(properties.callback && typeof(properties.callback) === 'function'){
                              properties.callback();
                           }
                           // ###END###
                        },
                        error: function (jqXHR) {
                           //### KCM ### Get response text instead of statusText if any
                           // #fnCancelSorting($dataTable, tbody, properties, 1, jqXHR.statusText);
                           var err = (jqXHR.responseText != "" ? jqXHR.responseText : jqXHR.statusText);
                           fnCancelSorting($dataTable, tbody, properties, 1, err);
                           // ### END ###
                        }
                  };
                  properties.fnUpdateAjaxRequest(oAjaxRequest, properties, $dataTable);
                  $.ajax(oAjaxRequest);
               } else {
                  fnMoveRows($dataTable, sSelector, oState.iCurrentPosition, oState.iNewPosition, oState.sDirection, ui.item.context.id, sGroup);
               }

            }
         });
      });

      return this;
   };

   // Attach RowReordering to DataTables so it can be accessed as an 'extra'
   $.fn.dataTable.rowReordering = $.fn.rowReordering;
   $.fn.DataTable.rowReordering = $.fn.rowReordering;

   // DataTables 1.10 API method aliases
   if ( $.fn.dataTable.Api ) {
      var Api = $.fn.dataTable.Api;
      Api.register( 'rowReordering()', $.fn.rowReordering );
   }
})(jQuery);