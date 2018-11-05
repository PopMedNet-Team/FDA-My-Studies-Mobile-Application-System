/*
 * Copyright (c) 2007-2017 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */

// NOTE labkey.js should NOT depend on any external libraries like ExtJS

if (typeof LABKEY == "undefined")
{
    /**
     * @namespace Namespace used to encapsulate LabKey core API and utilities.
     */
    LABKEY = new function()
    {
        var configs = {
            container: undefined,
            contextPath: "",
            DataRegions: {},
            devMode: false,
            demoMode: false,
            dirty: false,
            isDocumentClosed: false,
            extJsRoot: "ext-3.4.1",
            extJsRoot_42: "ext-4.2.1",
            extThemeRoot: "labkey-ext-theme",
            extThemeName_42: "seattle",
            extThemeRoot_42: "ext-theme",
            fieldMarker: '@',
            hash: 0,
            imagePath: "",
            requestedCssFiles: {},
            submit: false,
            unloadMessage: "You will lose any changes made to this page.",
            verbose: false,
            widget: {}
        };

        // private variables not configurable
        var _requestedCssFiles = {};

        // prepare null console to avoid errors in IE 11 and earlier, which only makes console available when the dev tools are open
        (function(){
            var method;
            var noop = function () {};
            var methods = [
                'assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error',
                'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log',
                'markTimeline', 'profile', 'profileEnd', 'table', 'time', 'timeEnd',
                'timeline', 'timelineEnd', 'timeStamp', 'trace', 'warn'
            ];
            var length = methods.length;
            var console = (window.console = window.console || {});

            while (length--) {
                method = methods[length];

                // Only stub undefined methods.
                if (!console[method]) {
                    console[method] = noop;
                }
            }
        })();

        // private caching mechanism for script loading
        var ScriptCache = function()
        {
            var cache = {};

            var callbacksOnCache = function(key)
            {
                // console.log('calling --', key);
                var cbs = cache[key];

                // set the cache to hit
                cache[key] = true;

                // Tell mothership.js to hook event callbacks
                if (LABKEY.Mothership)
                {
                    if (key.indexOf(configs.extJsRoot + "/ext-all") == 0)
                        LABKEY.Mothership.hookExt3();

                    if (key.indexOf(configs.extJsRoot_42 + "/ext-all") == 0)
                        LABKEY.Mothership.hookExt4();
                }

                // call on the callbacks who have been waiting for this resource
                if (isArray(cbs))
                {
                    var cb;
                    for (var c=0; c < cbs.length; c++)
                    {
                        cb = cbs[c];
                        handle(cb.fn, cb.scope);
                    }
                }
            };

            var inCache = function(key)
            {
                // console.log('hit --', key);
                return cache[key] === true;
            };

            var inFlightCache = function(key)
            {
                return isArray(cache[key]);
            };

            var loadCache = function(key, cb, s)
            {
                // console.log('miss --', key);
                // The value as an array denotes the cache resource is in flight
                if (!cache[key])
                    cache[key] = [];

                if (isFunction(cb))
                    cache[key].push({fn: cb, scope: s});
            };

            return {
                callbacksOnCache: callbacksOnCache,
                inCache: inCache,
                inFlightCache: inFlightCache,
                loadCache: loadCache
            };
        };

        // instance of scripting cache used by public methods
        var scriptCache = new ScriptCache();

        // Public Method Definitions

        var addElemToHead = function(elemName, attributes)
        {
            var elem = document.createElement(elemName);
            for (var a in attributes) {
                if (attributes.hasOwnProperty(a)) {
                    elem[a] = attributes[a];
                }
            }
            return document.getElementsByTagName("head")[0].appendChild(elem);
        };

        var addMarkup = function(html)
        {
            if (configs.isDocumentClosed)
            {
                var elem = document.createElement("div");
                elem.innerHTML = html;
                document.body.appendChild(elem.firstChild);
            }
            else
                document.write(html);
        };

        //private. used to append additional module context objects for AJAXd views
        var applyModuleContext = function(ctx) {
            for (var mn in ctx) {
                if (ctx.hasOwnProperty(mn)) {
                    LABKEY.moduleContext[mn.toLowerCase()] = ctx[mn];
                }
            }
        };

        var beforeunload = function (dirtyCallback, scope, msg)
        {
            return function () {
                if (!getSubmit() && (isDirty() || (dirtyCallback && dirtyCallback.call(scope)))) {
                    return msg || configs.unloadMessage;
                }
            };
        };

        var createElement = function(tag, innerHTML, attributes)
        {
            var e = document.createElement(tag);
            if (innerHTML)
                e.innerHTML = innerHTML;
            if (attributes)
            {
                for (var att in attributes)
                {
                    if (attributes.hasOwnProperty(att))
                    {
                        try
                        {
                            e[att] = attributes[att];
                        }
                        catch (x)
                        {
                            console.log(x); // e['style'] is read-only in old firefox
                        }
                    }
                }
            }
            return e;
        };

        var getModuleContext = function(moduleName) {
            return LABKEY.moduleContext[moduleName.toLowerCase()];
        };

        var getModuleProperty = function(moduleName, property) {
            var ctx = getModuleContext(moduleName);
            if (!ctx) {
                return null;
            }
            return ctx[property];
        };

        var getSubmit = function()
        {
            return configs.submit;
        };

        // simple callback handler that will type check then call with scope
        var handle = function(callback, scope)
        {
            if (isFunction(callback))
            {
                callback.call(scope || this);
            }
        };

        // If we're in demo mode, replace each ID with an equal length string of "*".  This code should match DemoMode.id().
        var id = function(id)
        {
            if (configs.demoMode)
            {
                return new Array(id.length + 1).join("*");
            }
            return id;
        };

        var init = function(config)
        {
            for (var p in config)
            {
                //TODO: we should be trying to seal some of these objects, or at least wrap them to make them harder to manipulate
                if (config.hasOwnProperty(p)) {
                    configs[p] = config[p];
                    LABKEY[p] = config[p];
                }
            }
            if ("Security" in LABKEY)
                LABKEY.Security.currentUser = LABKEY.user;
        };

        var isArray = function(value)
        {
            return Object.prototype.toString.call(value) === "[object Array]";
        };

        var isBoolean = function(value)
        {
            return typeof value === "boolean";
        };

        var isDirty = function()
        {
            return configs.dirty;
        };

        var isFunction = function(value)
        {
            return typeof value === "function";
        };

        var isLibrary = function(file)
        {
            return file && (file.indexOf('.') === -1 || file.indexOf('.lib.xml') > -1);
        };

        var loadScripts = function()
        {
            configs.isDocumentClosed = true;
        };

        var loadedScripts = function()
        {
            for (var i=0; i < arguments.length; i++)
            {
                if (isArray(arguments[i]))
                {
                    for (var j=0; j < arguments[i].length; j++)
                    {
                        scriptCache.callbacksOnCache(arguments[i][j]);
                    }
                }
                else
                {
                    scriptCache.callbacksOnCache(arguments[i]);
                }
            }
            return true;
        };

        var qs = function(params)
        {
            if (!params)
                return '';

            var qs = '', and = '', pv, p;

            for (p in params)
            {
                if (params.hasOwnProperty(p))
                {
                    pv = params[p];

                    if (pv === null || pv === undefined)
                        pv = '';

                    if (isArray(pv))
                    {
                        for (var i=0; i < pv.length; i++)
                        {
                            qs += and + encodeURIComponent(p) + '=' + encodeURIComponent(pv[i]);
                            and = '&';
                        }
                    }
                    else
                    {
                        qs += and + encodeURIComponent(p) + '=' + encodeURIComponent(pv);
                        and = '&';
                    }
                }
            }

            return qs;
        };

        // So as not to confuse with native support for fetch()
        var _fetch = function(url, params, success, failure)
        {
            var xhr = new XMLHttpRequest();
            var _url = url + (url.indexOf('?') === -1 ? '?' : '&') + qs(params);

            xhr.onreadystatechange = function()
            {
                if (xhr.readyState === 4)
                {
                    var _success = (xhr.status >= 200 && xhr.status < 300) || xhr.status == 304;
                    _success ? success(xhr) : failure(xhr);
                }
            };

            xhr.open('GET', _url, true);

            xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
            if (LABKEY.CSRF)
                xhr.setRequestHeader('X-LABKEY-CSRF', LABKEY.CSRF);

            xhr.send(null);

            return xhr;
        };

        var requiresCss = function(file)
        {
            if (isArray(file))
            {
                for (var i=0;i<file.length;i++)
                    requiresCss(file[i]);
                return;
            }

            if (file.indexOf('/') == 0)
            {
                file = file.substring(1);
            }

            var key = file,
                fullPath;

            if (!_requestedCssFiles[key])
            {
                _requestedCssFiles[key] = true;

                // Support both LabKey and external CSS files
                if (file.substr(0, 4) != "http")
                {
                    // local files
                    fullPath = configs.contextPath + "/" + file + '?' + configs.hash;
                }
                else
                {
                    // external files
                    fullPath = file;
                }

                addElemToHead("link", {
                    type: "text/css",
                    rel: "stylesheet",
                    href: fullPath
                });
            }
        };

        var requestedCssFiles = function()
        {
            var ret = arguments.length > 0 && _requestedCssFiles[arguments[0]];
            for (var i=0; i < arguments.length ; i++)
                _requestedCssFiles[arguments[i]] = true;
            return ret;
        };

        var requiresClientAPI = function(callback, scope)
        {
            // backwards compat for 'immediate'
            if (arguments.length > 0 && isBoolean(arguments[0]))
            {
                callback = arguments[1];
                scope = arguments[2];
            }

            requiresLib('clientapi', function()
            {
                requiresExt3ClientAPI(callback, scope);
            });
        };

        var requiresExt3 = function(callback, scope)
        {
            // backwards compat for 'immediate'
            if (arguments.length > 0 && isBoolean(arguments[0]))
            {
                callback = arguments[1];
                scope = arguments[2];
            }

            if (window.Ext)
            {
                handle(callback, scope);
            }
            else
            {
                requiresLib('Ext3', callback, scope);
            }
        };

        var requiresExt3ClientAPI = function(callback, scope)
        {
            // backwards compat for 'immediate'
            if (arguments.length > 0 && isBoolean(arguments[0]))
            {
                callback = arguments[1];
                scope = arguments[2];
            }

            requiresExt3(function()
            {
                requiresLib('clientapi/ext3', callback, scope);
            });
        };

        var requiresExt4ClientAPI = function(callback, scope)
        {
            // backwards compat for 'immediate'
            if (arguments.length > 0 && isBoolean(arguments[0]))
            {
                callback = arguments[1];
                scope = arguments[2];
            }

            requiresExt4Sandbox(function()
            {
                requiresLib('Ext4ClientApi', callback, scope);
            });
        };

        var requiresExt4Sandbox = function(callback, scope)
        {
            // backwards compat for 'immediate'
            if (arguments.length > 0 && isBoolean(arguments[0]))
            {
                callback = arguments[1];
                scope = arguments[2];
            }

            if (window.Ext4)
            {
                handle(callback, scope);
            }
            else
            {
                requiresLib('Ext4', callback, scope);
            }
        };

        var requiresLib = function(lib, callback, scope)
        {
            if (!lib)
            {
                handle(callback, scope);
                return;
            }

            var _lib = lib.split('.lib.xml')[0];

            // in case _lib is now empty
            if (!_lib)
            {
                handle(callback, scope);
                return;
            }

            if (scriptCache.inCache(_lib))
            {
                handle(callback, scope);
                return;
            }
            else if (scriptCache.inFlightCache(_lib))
            {
                scriptCache.loadCache(_lib, callback, scope);
                return;
            }
            else
            {
                scriptCache.loadCache(_lib, callback, scope);
            }

            var cacheLoader = function()
            {
                scriptCache.callbacksOnCache(_lib);
            };

            _fetch('core-loadLibrary.api', {
                library: _lib
            }, function(data) {
                // success
                var json = JSON.parse(data.responseText);
                var definition = json['libraries'][_lib];

                if (definition)
                {
                    var styles = [];
                    var scripts = [];
                    for (var d=0; d < definition.length; d++)
                    {
                        if (definition[d].indexOf('.css') > -1)
                        {
                            styles.push(definition[d]);
                        }
                        else
                        {
                            scripts.push(definition[d]);
                        }
                    }

                    LABKEY.requiresCss(styles);
                    LABKEY.requiresScript(scripts, cacheLoader, undefined, true /* inOrder, sadly */);
                }
                else
                {
                    throw new Error('Failed to retrieve library definition \"' + _lib + '\"');
                }
            }, function() {
                // failure
                throw new Error('Failed to load library: \"' + _lib + '\"');
            });
        };

        var requiresScript = function(file, callback, scope, inOrder)
        {
            if (arguments.length === 0)
            {
                throw "LABKEY.requiresScript() requires the 'file' parameter.";
            }
            else if (!file)
            {
                throw "LABKEY.requiresScript() invalid 'file' argument.";
            }

            // backwards compat for 'immediate'
            if (arguments.length > 1 && isBoolean(arguments[1]))
            {
                callback = arguments[2];
                scope = arguments[3];
                inOrder = arguments[4];
            }

            if (isArray(file))
            {
                var requestedLength = file.length;
                var loaded = 0;

                if (requestedLength === 0)
                {
                    handle(callback, scope);
                }
                else if (inOrder)
                {
                    var chain = function()
                    {
                        loaded++;
                        if (loaded === requestedLength)
                        {
                            handle(callback, scope);
                        }
                        else if (loaded < requestedLength)
                            requiresScript(file[loaded], chain, undefined, true);
                    };

                    if (scriptCache.inCache(file[loaded]))
                    {
                        chain();
                    }
                    else
                        requiresScript(file[loaded], chain, undefined, true);
                }
                else
                {
                    // request all the scripts (order does not matter)
                    var allDone = function()
                    {
                        loaded++;
                        if (loaded === requestedLength)
                        {
                            handle(callback, scope);
                        }
                    };

                    for (var i = 0; i < file.length; i++)
                    {
                        if (scriptCache.inCache(file[i]))
                        {
                            allDone();
                        }
                        else
                            requiresScript(file[i], allDone);
                    }
                }
                return;
            }

            if (isLibrary(file))
            {
                if (file === 'Ext3')
                {
                    requiresExt3(callback, scope);
                }
                else if (file === 'Ext4')
                {
                    requiresExt4Sandbox(callback, scope);
                }
                else
                {
                    requiresLib(file, callback, scope);
                }
                return;
            }

            if (file.indexOf('/') === 0)
            {
                file = file.substring(1);
            }

            if (scriptCache.inCache(file))
            {
                // cache hit -- script is loaded and ready to go
                handle(callback, scope);
                return;
            }
            else if (scriptCache.inFlightCache(file))
            {
                // cache miss -- in flight
                scriptCache.loadCache(file, callback, scope);
                return;
            }
            else
            {
                // cache miss
                scriptCache.loadCache(file, callback, scope);
            }

            // although FireFox and Safari allow scripts to use the DOM
            // during parse time, IE does not. So if the document is
            // closed, use the DOM to create a script element and append it
            // to the head element. Otherwise (still parsing), use document.write()

            // Support both LabKey and external JavaScript files
            var src = file.substr(0, 4) != "http" ? configs.contextPath + "/" + file + '?' + configs.hash : file;

            var cacheLoader = function()
            {
                scriptCache.callbacksOnCache(file);
            };

            if (configs.isDocumentClosed || callback)
            {
                //create a new script element and append it to the head element
                var script = addElemToHead("script", {
                    src: src,
                    type: "text/javascript"
                });

                // IE has a different way of handling <script> loads
                if (script.readyState)
                {
                    script.onreadystatechange = function() {
                        if (script.readyState == "loaded" || script.readyState == "complete") {
                            script.onreadystatechange = null;
                            cacheLoader();
                        }
                    };
                }
                else
                {
                    script.onload = cacheLoader;
                }
            }
            else
            {
                document.write('\n<script type="text/javascript" src="' + src + '"></script>\n');
                cacheLoader();
            }
        };

        var requiresVisualization = function(callback, scope)
        {
            requiresExt4Sandbox(function() {
                requiresLib('vis/vis', callback, scope);
            }, scope);
        };

        var setDirty = function (dirty)
        {
            configs.dirty = (dirty ? true : false); // only set to boolean
        };

        var setSubmit = function (submit)
        {
            configs.submit = (submit ? true : false); // only set to boolean
        };

        var showNavTrail = function()
        {
            var elem = document.getElementById("navTrailAncestors");
            if(elem)
                elem.style.visibility = "visible";
            elem = document.getElementById("labkey-nav-trail-current-page");
            if(elem)
                elem.style.visibility = "visible";
        };

        return {
            /**
             * A collection of properties related to the "current" LabKey Server container scope.
             * The properties are as follows:
             * <ul>
             *     <li>formats: Java formatting strings as set in /admin-projectSettings.view
             *         <ul>
             *             <li>dateFormat: The display format for dates</li>
             *             <li>dateTimeFormat: The display format for date-times</li>
             *             <li>numberFormat: The display format for numbers</li>
             *         </ul>
             *     </li>
             * </ul>
             */
            container: configs.container,

            /**
             * This callback type is called 'requireCallback' and is displayed as a global symbol
             *
             * @callback requireCallback
             */

            /**
             * The DataRegion class allows you to interact with LabKey grids,
             * including querying and modifying selection state, filters, and more.
             * @field
             */
            DataRegions: configs.DataRegions,

            demoMode: configs.demoMode,
            devMode: configs.devMode,
            dirty: configs.dirty,
            extJsRoot: configs.extJsRoot,
            extJsRoot_42: configs.extJsRoot_42,
            extThemeRoot: configs.extThemeRoot,
            fieldMarker: configs.fieldMarker,
            hash: configs.hash,
            imagePath: configs.imagePath,
            submit: configs.submit,
            unloadMessage: configs.unloadMessage,
            verbose: configs.verbose,
            widget: configs.widget,

            /** @field */
            contextPath: configs.contextPath,

            /**
             * Appends an element to the head of the document
             * @private
             * @param {String} elemName First argument for docoument.createElement
             * @param {Object} [attributes]
             * @returns {*}
             */
            addElemToHead: addElemToHead,

            // TODO: Eligible for removal after util.js is migrated
            addMarkup: addMarkup,
            applyModuleContext: applyModuleContext,
            beforeunload: beforeunload,
            createElement: createElement,

            /**
             * @function
             * @param {String} moduleName The name of the module
             * @returns {Object} The context object for this module.  The current view must have specifically requested
             * the context for this module in its view XML
             */
            getModuleContext: getModuleContext,

            /**
             * @function
             * @param {String} moduleName The name of the module
             * @param {String} property The property name to return
             * @returns {String} The value of the module property.  Will return null if the property has not been set.
             */
            getModuleProperty: getModuleProperty,
            getSubmit: getSubmit,
            id: id,
            init: init,
            isDirty: isDirty,
            loadScripts: loadScripts,
            loadedScripts: loadedScripts,

            /**
             * Loads a CSS file from the server.
             * @function
             * @param {(string|string[])} file - The path of the CSS file to load
             * @example
             &lt;script type="text/javascript"&gt;
                LABKEY.requiresCss("myModule/myFile.css");
             &lt;/script&gt;
             */
            requiresCss: requiresCss,
            requestedCssFiles: requestedCssFiles,
            requiresClientAPI: requiresClientAPI,

            /**
             * This can be added to any LABKEY page in order to load ExtJS 3.  This is the preferred method to declare Ext3 usage
             * from wiki pages.  For HTML or JSP pages defined in a module, see our <a href="https://www.labkey.org/wiki/home/Documentation/page.view?name=ext4Development">documentation</a> on declaration of client dependencies.
             * @function
             * @param {boolean} [immediate=true] - True to load the script immediately; false will defer script loading until the page has been downloaded.
             * @param {requireCallback} [callback] - Callback for when all dependencies are loaded.
             * @param {Object} [scope] - Scope of callback.
             * @example
             &lt;script type="text/javascript"&gt;
                LABKEY.requiresExt3(true, function() {
                    Ext.onReady(function() {
                        // Ext 3 is loaded and ready
                    });
                });
             &lt;/script&gt;
             */
            requiresExt3: requiresExt3,

            /**
             * This can be added to any LABKEY page in order to load the LabKey ExtJS 3 Client API.
             * @function
             * @param {boolean} [immediate=true] - True to load the script immediately; false will defer script loading until the page has been downloaded.
             * @param {requireCallback} [callback] - Callback for when all dependencies are loaded.
             * @param {Object} [scope] - Scope of callback.
             * @example
             &lt;script type="text/javascript"&gt;
                 LABKEY.requiresExt3ClientAPI(true, function() {
                    // your code here
                 });
             &lt;/script&gt;
             */
            requiresExt3ClientAPI: requiresExt3ClientAPI,

            /**
             * This can be added to any LABKEY page in order to load the LabKey ExtJS 4 Client API. This primarily
             * consists of a set of utility methods {@link LABKEY.ext4.Util} and an extended Ext.data.Store {@link LABKEY.ext4.data.Store}.
             * It will load ExtJS 4 as a dependency.
             * @function
             * @param {boolean} [immediate=true] - True to load the script immediately; false will defer script loading until the page has been downloaded.
             * @param {requireCallback} [callback] - Callback for when all dependencies are loaded.
             * @param {Object} [scope] - Scope of callback.
             * @example
             &lt;script type="text/javascript"&gt;
                 LABKEY.requiresExt4ClientAPI(true, function() {
                    // your code here
                 });
             &lt;/script&gt;
             */
            requiresExt4ClientAPI: requiresExt4ClientAPI,

            /**
             * This can be added to any LABKEY page in order to load ExtJS 4.  This is the preferred method to declare Ext4 usage
             * from wiki pages.  For HTML or JSP pages defined in a module, see our <a href="https://www.labkey.org/wiki/home/Documentation/page.view?name=ext4Development">documentation</a> on declaration of client dependencies.
             * @function
             * @param {boolean} [immediate=true] - True to load the script immediately; false will defer script loading until the page has been downloaded.
             * @param {requireCallback} [callback] - Callback for when all dependencies are loaded.
             * @param {Object} [scope] - Scope of callback.
             * @example
             &lt;script type="text/javascript"&gt;
                 LABKEY.requiresExt4Sandbox(true, function() {
                    Ext4.onReady(function(){
                        // Ext4 is loaded and ready
                    });
                 });
             &lt;/script&gt;
             */
            requiresExt4Sandbox: requiresExt4Sandbox,

            /**
             * Deprecated.  Use LABKEY.requiresExt3 instead.
             * @function
             * @private
             */
            requiresExtJs: requiresExt3,

            /**
             * Loads JavaScript file(s) from the server.
             * @function
             * @param {(string|string[])} file - A file or Array of files to load.
             * @param {Function} [callback] - Callback for when all dependencies are loaded.
             * @param {Object} [scope] - Scope of callback.
             * @param {boolean} [inOrder=false] - True to load the scripts in the order they are passed in. Default is false.
             * @example
             &lt;script type="text/javascript"&gt;
                LABKEY.requiresScript("myModule/myScript.js", true, function() {
                    // your script is loaded
                });
             &lt;/script&gt;
             */
            requiresScript: requiresScript,
            requiresVisualization: requiresVisualization,
            setDirty: setDirty,
            setSubmit: setSubmit,
            showNavTrail: showNavTrail
        }
    };

}
