var Prototype={Version:"1.7",Browser:(function(){var b=navigator.userAgent;var a=Object.prototype.toString.call(window.opera)=="[object Opera]";return{IE:!!window.attachEvent&&!a,Opera:a,WebKit:b.indexOf("AppleWebKit/")>-1,Gecko:b.indexOf("Gecko")>-1&&b.indexOf("KHTML")===-1,MobileSafari:/Apple.*Mobile/.test(b)}})(),BrowserFeatures:{XPath:!!document.evaluate,SelectorsAPI:!!document.querySelector,ElementExtensions:(function(){var a=window.Element||window.HTMLElement;return !!(a&&a.prototype)})(),SpecificElementExtensions:(function(){if(typeof window.HTMLDivElement!=="undefined"){return true}var c=document.createElement("div"),b=document.createElement("form"),a=false;if(c.__proto__&&(c.__proto__!==b.__proto__)){a=true}c=b=null;return a})()},ScriptFragment:"<script[^>]*>([\\S\\s]*?)<\/script>",JSONFilter:/^\/\*-secure-([\s\S]*)\*\/\s*$/,emptyFunction:function(){},K:function(a){return a}};if(Prototype.Browser.MobileSafari){Prototype.BrowserFeatures.SpecificElementExtensions=false}var Abstract={};var Try={these:function(){var c;for(var b=0,d=arguments.length;b<d;b++){var a=arguments[b];try{c=a();break}catch(f){}}return c}};var Class=(function(){var d=(function(){for(var e in {toString:1}){if(e==="toString"){return false}}return true})();function a(){}function b(){var h=null,g=$A(arguments);if(Object.isFunction(g[0])){h=g.shift()}function e(){this.initialize.apply(this,arguments)}Object.extend(e,Class.Methods);e.superclass=h;e.subclasses=[];if(h){a.prototype=h.prototype;e.prototype=new a;h.subclasses.push(e)}for(var f=0,j=g.length;f<j;f++){e.addMethods(g[f])}if(!e.prototype.initialize){e.prototype.initialize=Prototype.emptyFunction}e.prototype.constructor=e;return e}function c(l){var g=this.superclass&&this.superclass.prototype,f=Object.keys(l);if(d){if(l.toString!=Object.prototype.toString){f.push("toString")}if(l.valueOf!=Object.prototype.valueOf){f.push("valueOf")}}for(var e=0,h=f.length;e<h;e++){var k=f[e],j=l[k];if(g&&Object.isFunction(j)&&j.argumentNames()[0]=="$super"){var m=j;j=(function(i){return function(){return g[i].apply(this,arguments)}})(k).wrap(m);j.valueOf=m.valueOf.bind(m);j.toString=m.toString.bind(m)}this.prototype[k]=j}return this}return{create:b,Methods:{addMethods:c}}})();(function(){var C=Object.prototype.toString,B="Null",o="Undefined",v="Boolean",f="Number",s="String",H="Object",t="[object Function]",y="[object Boolean]",g="[object Number]",l="[object String]",h="[object Array]",x="[object Date]",i=window.JSON&&typeof JSON.stringify==="function"&&JSON.stringify(0)==="0"&&typeof JSON.stringify(Prototype.K)==="undefined";function k(J){switch(J){case null:return B;case (void 0):return o}var I=typeof J;switch(I){case"boolean":return v;case"number":return f;case"string":return s}return H}function z(I,K){for(var J in K){I[J]=K[J]}return I}function G(I){try{if(c(I)){return"undefined"}if(I===null){return"null"}return I.inspect?I.inspect():String(I)}catch(J){if(J instanceof RangeError){return"..."}throw J}}function D(I){return F("",{"":I},[])}function F(R,O,P){var Q=O[R],N=typeof Q;if(k(Q)===H&&typeof Q.toJSON==="function"){Q=Q.toJSON(R)}var K=C.call(Q);switch(K){case g:case y:case l:Q=Q.valueOf()}switch(Q){case null:return"null";case true:return"true";case false:return"false"}N=typeof Q;switch(N){case"string":return Q.inspect(true);case"number":return isFinite(Q)?String(Q):"null";case"object":for(var J=0,I=P.length;J<I;J++){if(P[J]===Q){throw new TypeError()}}P.push(Q);var M=[];if(K===h){for(var J=0,I=Q.length;J<I;J++){var L=F(J,Q,P);M.push(typeof L==="undefined"?"null":L)}M="["+M.join(",")+"]"}else{var S=Object.keys(Q);for(var J=0,I=S.length;J<I;J++){var R=S[J],L=F(R,Q,P);if(typeof L!=="undefined"){M.push(R.inspect(true)+":"+L)}}M="{"+M.join(",")+"}"}P.pop();return M}}function w(I){return JSON.stringify(I)}function j(I){return $H(I).toQueryString()}function p(I){return I&&I.toHTML?I.toHTML():String.interpret(I)}function r(I){if(k(I)!==H){throw new TypeError()}var J=[];for(var K in I){if(I.hasOwnProperty(K)){J.push(K)}}return J}function d(I){var J=[];for(var K in I){J.push(I[K])}return J}function A(I){return z({},I)}function u(I){return !!(I&&I.nodeType==1)}function m(I){return C.call(I)===h}var b=(typeof Array.isArray=="function")&&Array.isArray([])&&!Array.isArray({});if(b){m=Array.isArray}function e(I){return I instanceof Hash}function a(I){return C.call(I)===t}function n(I){return C.call(I)===l}function q(I){return C.call(I)===g}function E(I){return C.call(I)===x}function c(I){return typeof I==="undefined"}z(Object,{extend:z,inspect:G,toJSON:i?w:D,toQueryString:j,toHTML:p,keys:Object.keys||r,values:d,clone:A,isElement:u,isArray:m,isHash:e,isFunction:a,isString:n,isNumber:q,isDate:E,isUndefined:c})})();Object.extend(Function.prototype,(function(){var k=Array.prototype.slice;function d(o,l){var n=o.length,m=l.length;while(m--){o[n+m]=l[m]}return o}function i(m,l){m=k.call(m,0);return d(m,l)}function g(){var l=this.toString().match(/^[\s\(]*function[^(]*\(([^)]*)\)/)[1].replace(/\/\/.*?[\r\n]|\/\*(?:.|[\r\n])*?\*\//g,"").replace(/\s+/g,"").split(",");return l.length==1&&!l[0]?[]:l}function h(n){if(arguments.length<2&&Object.isUndefined(arguments[0])){return this}var l=this,m=k.call(arguments,1);return function(){var o=i(m,arguments);return l.apply(n,o)}}function f(n){var l=this,m=k.call(arguments,1);return function(p){var o=d([p||window.event],m);return l.apply(n,o)}}function j(){if(!arguments.length){return this}var l=this,m=k.call(arguments,0);return function(){var n=i(m,arguments);return l.apply(this,n)}}function e(n){var l=this,m=k.call(arguments,1);n=n*1000;return window.setTimeout(function(){return l.apply(l,m)},n)}function a(){var l=d([0.01],arguments);return this.delay.apply(this,l)}function c(m){var l=this;return function(){var n=d([l.bind(this)],arguments);return m.apply(this,n)}}function b(){if(this._methodized){return this._methodized}var l=this;return this._methodized=function(){var m=d([this],arguments);return l.apply(null,m)}}return{argumentNames:g,bind:h,bindAsEventListener:f,curry:j,delay:e,defer:a,wrap:c,methodize:b}})());(function(c){function b(){return this.getUTCFullYear()+"-"+(this.getUTCMonth()+1).toPaddedString(2)+"-"+this.getUTCDate().toPaddedString(2)+"T"+this.getUTCHours().toPaddedString(2)+":"+this.getUTCMinutes().toPaddedString(2)+":"+this.getUTCSeconds().toPaddedString(2)+"Z"}function a(){return this.toISOString()}if(!c.toISOString){c.toISOString=b}if(!c.toJSON){c.toJSON=a}})(Date.prototype);RegExp.prototype.match=RegExp.prototype.test;RegExp.escape=function(a){return String(a).replace(/([.*+?^=!:${}()|[\]\/\\])/g,"\\$1")};var PeriodicalExecuter=Class.create({initialize:function(b,a){this.callback=b;this.frequency=a;this.currentlyExecuting=false;this.registerCallback()},registerCallback:function(){this.timer=setInterval(this.onTimerEvent.bind(this),this.frequency*1000)},execute:function(){this.callback(this)},stop:function(){if(!this.timer){return}clearInterval(this.timer);this.timer=null},onTimerEvent:function(){if(!this.currentlyExecuting){try{this.currentlyExecuting=true;this.execute();this.currentlyExecuting=false}catch(a){this.currentlyExecuting=false;throw a}}}});Object.extend(String,{interpret:function(a){return a==null?"":String(a)},specialChar:{"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r","\\":"\\\\"}});Object.extend(String.prototype,(function(){var NATIVE_JSON_PARSE_SUPPORT=window.JSON&&typeof JSON.parse==="function"&&JSON.parse('{"test": true}').test;function prepareReplacement(replacement){if(Object.isFunction(replacement)){return replacement}var template=new Template(replacement);return function(match){return template.evaluate(match)}}function gsub(pattern,replacement){var result="",source=this,match;replacement=prepareReplacement(replacement);if(Object.isString(pattern)){pattern=RegExp.escape(pattern)}if(!(pattern.length||pattern.source)){replacement=replacement("");return replacement+source.split("").join(replacement)+replacement}while(source.length>0){if(match=source.match(pattern)){result+=source.slice(0,match.index);result+=String.interpret(replacement(match));source=source.slice(match.index+match[0].length)}else{result+=source,source=""}}return result}function sub(pattern,replacement,count){replacement=prepareReplacement(replacement);count=Object.isUndefined(count)?1:count;return this.gsub(pattern,function(match){if(--count<0){return match[0]}return replacement(match)})}function scan(pattern,iterator){this.gsub(pattern,iterator);return String(this)}function truncate(length,truncation){length=length||30;truncation=Object.isUndefined(truncation)?"...":truncation;return this.length>length?this.slice(0,length-truncation.length)+truncation:String(this)}function strip(){return this.replace(/^\s+/,"").replace(/\s+$/,"")}function stripTags(){return this.replace(/<\w+(\s+("[^"]*"|'[^']*'|[^>])+)?>|<\/\w+>/gi,"")}function stripScripts(){return this.replace(new RegExp(Prototype.ScriptFragment,"img"),"")}function extractScripts(){var matchAll=new RegExp(Prototype.ScriptFragment,"img"),matchOne=new RegExp(Prototype.ScriptFragment,"im");return(this.match(matchAll)||[]).map(function(scriptTag){return(scriptTag.match(matchOne)||["",""])[1]})}function evalScripts(){return this.extractScripts().map(function(script){return eval(script)})}function escapeHTML(){return this.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;")}function unescapeHTML(){return this.stripTags().replace(/&lt;/g,"<").replace(/&gt;/g,">").replace(/&amp;/g,"&")}function toQueryParams(separator){var match=this.strip().match(/([^?#]*)(#.*)?$/);if(!match){return{}}return match[1].split(separator||"&").inject({},function(hash,pair){if((pair=pair.split("="))[0]){var key=decodeURIComponent(pair.shift()),value=pair.length>1?pair.join("="):pair[0];if(value!=undefined){value=decodeURIComponent(value)}if(key in hash){if(!Object.isArray(hash[key])){hash[key]=[hash[key]]}hash[key].push(value)}else{hash[key]=value}}return hash})}function toArray(){return this.split("")}function succ(){return this.slice(0,this.length-1)+String.fromCharCode(this.charCodeAt(this.length-1)+1)}function times(count){return count<1?"":new Array(count+1).join(this)}function camelize(){return this.replace(/-+(.)?/g,function(match,chr){return chr?chr.toUpperCase():""})}function capitalize(){return this.charAt(0).toUpperCase()+this.substring(1).toLowerCase()}function underscore(){return this.replace(/::/g,"/").replace(/([A-Z]+)([A-Z][a-z])/g,"$1_$2").replace(/([a-z\d])([A-Z])/g,"$1_$2").replace(/-/g,"_").toLowerCase()}function dasherize(){return this.replace(/_/g,"-")}function inspect(useDoubleQuotes){var escapedString=this.replace(/[\x00-\x1f\\]/g,function(character){if(character in String.specialChar){return String.specialChar[character]}return"\\u00"+character.charCodeAt().toPaddedString(2,16)});if(useDoubleQuotes){return'"'+escapedString.replace(/"/g,'\\"')+'"'}return"'"+escapedString.replace(/'/g,"\\'")+"'"}function unfilterJSON(filter){return this.replace(filter||Prototype.JSONFilter,"$1")}function isJSON(){var str=this;if(str.blank()){return false}str=str.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@");str=str.replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]");str=str.replace(/(?:^|:|,)(?:\s*\[)+/g,"");return(/^[\],:{}\s]*$/).test(str)}function evalJSON(sanitize){var json=this.unfilterJSON(),cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;if(cx.test(json)){json=json.replace(cx,function(a){return"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})}try{if(!sanitize||json.isJSON()){return eval("("+json+")")}}catch(e){}throw new SyntaxError("Badly formed JSON string: "+this.inspect())}function parseJSON(){var json=this.unfilterJSON();return JSON.parse(json)}function include(pattern){return this.indexOf(pattern)>-1}function startsWith(pattern){return this.lastIndexOf(pattern,0)===0}function endsWith(pattern){var d=this.length-pattern.length;return d>=0&&this.indexOf(pattern,d)===d}function empty(){return this==""}function blank(){return/^\s*$/.test(this)}function interpolate(object,pattern){return new Template(this,pattern).evaluate(object)}return{gsub:gsub,sub:sub,scan:scan,truncate:truncate,strip:String.prototype.trim||strip,stripTags:stripTags,stripScripts:stripScripts,extractScripts:extractScripts,evalScripts:evalScripts,escapeHTML:escapeHTML,unescapeHTML:unescapeHTML,toQueryParams:toQueryParams,parseQuery:toQueryParams,toArray:toArray,succ:succ,times:times,camelize:camelize,capitalize:capitalize,underscore:underscore,dasherize:dasherize,inspect:inspect,unfilterJSON:unfilterJSON,isJSON:isJSON,evalJSON:NATIVE_JSON_PARSE_SUPPORT?parseJSON:evalJSON,include:include,startsWith:startsWith,endsWith:endsWith,empty:empty,blank:blank,interpolate:interpolate}})());var Template=Class.create({initialize:function(a,b){this.template=a.toString();this.pattern=b||Template.Pattern},evaluate:function(a){if(a&&Object.isFunction(a.toTemplateReplacements)){a=a.toTemplateReplacements()}return this.template.gsub(this.pattern,function(d){if(a==null){return(d[1]+"")}var f=d[1]||"";if(f=="\\"){return d[2]}var b=a,g=d[3],e=/^([^.[]+|\[((?:.*?[^\\])?)\])(\.|\[|$)/;d=e.exec(g);if(d==null){return f}while(d!=null){var c=d[1].startsWith("[")?d[2].replace(/\\\\]/g,"]"):d[1];b=b[c];if(null==b||""==d[3]){break}g=g.substring("["==d[3]?d[1].length:d[0].length);d=e.exec(g)}return f+String.interpret(b)})}});Template.Pattern=/(^|.|\r|\n)(#\{(.*?)\})/;var $break={};var Enumerable=(function(){function c(y,x){var w=0;try{this._each(function(A){y.call(x,A,w++)})}catch(z){if(z!=$break){throw z}}return this}function r(z,y,x){var w=-z,A=[],B=this.toArray();if(z<1){return B}while((w+=z)<B.length){A.push(B.slice(w,w+z))}return A.collect(y,x)}function b(y,x){y=y||Prototype.K;var w=true;this.each(function(A,z){w=w&&!!y.call(x,A,z);if(!w){throw $break}});return w}function i(y,x){y=y||Prototype.K;var w=false;this.each(function(A,z){if(w=!!y.call(x,A,z)){throw $break}});return w}function j(y,x){y=y||Prototype.K;var w=[];this.each(function(A,z){w.push(y.call(x,A,z))});return w}function t(y,x){var w;this.each(function(A,z){if(y.call(x,A,z)){w=A;throw $break}});return w}function h(y,x){var w=[];this.each(function(A,z){if(y.call(x,A,z)){w.push(A)}});return w}function g(z,y,x){y=y||Prototype.K;var w=[];if(Object.isString(z)){z=new RegExp(RegExp.escape(z))}this.each(function(B,A){if(z.match(B)){w.push(y.call(x,B,A))}});return w}function a(w){if(Object.isFunction(this.indexOf)){if(this.indexOf(w)!=-1){return true}}var x=false;this.each(function(y){if(y==w){x=true;throw $break}});return x}function q(x,w){w=Object.isUndefined(w)?null:w;return this.eachSlice(x,function(y){while(y.length<x){y.push(w)}return y})}function l(w,y,x){this.each(function(A,z){w=y.call(x,w,A,z)});return w}function v(x){var w=$A(arguments).slice(1);return this.map(function(y){return y[x].apply(y,w)})}function p(y,x){y=y||Prototype.K;var w;this.each(function(A,z){A=y.call(x,A,z);if(w==null||A>=w){w=A}});return w}function n(y,x){y=y||Prototype.K;var w;this.each(function(A,z){A=y.call(x,A,z);if(w==null||A<w){w=A}});return w}function e(z,x){z=z||Prototype.K;var y=[],w=[];this.each(function(B,A){(z.call(x,B,A)?y:w).push(B)});return[y,w]}function f(x){var w=[];this.each(function(y){w.push(y[x])});return w}function d(y,x){var w=[];this.each(function(A,z){if(!y.call(x,A,z)){w.push(A)}});return w}function m(x,w){return this.map(function(z,y){return{value:z,criteria:x.call(w,z,y)}}).sort(function(B,A){var z=B.criteria,y=A.criteria;return z<y?-1:z>y?1:0}).pluck("value")}function o(){return this.map()}function s(){var x=Prototype.K,w=$A(arguments);if(Object.isFunction(w.last())){x=w.pop()}var y=[this].concat(w).map($A);return this.map(function(A,z){return x(y.pluck(z))})}function k(){return this.toArray().length}function u(){return"#<Enumerable:"+this.toArray().inspect()+">"}return{each:c,eachSlice:r,all:b,every:b,any:i,some:i,collect:j,map:j,detect:t,findAll:h,select:h,filter:h,grep:g,include:a,member:a,inGroupsOf:q,inject:l,invoke:v,max:p,min:n,partition:e,pluck:f,reject:d,sortBy:m,toArray:o,entries:o,zip:s,size:k,inspect:u,find:t}})();function $A(c){if(!c){return[]}if("toArray" in Object(c)){return c.toArray()}var b=c.length||0,a=new Array(b);while(b--){a[b]=c[b]}return a}function $w(a){if(!Object.isString(a)){return[]}a=a.strip();return a?a.split(/\s+/):[]}Array.from=$A;(function(){var r=Array.prototype,m=r.slice,o=r.forEach;function b(w,v){for(var u=0,x=this.length>>>0;u<x;u++){if(u in this){w.call(v,this[u],u,this)}}}if(!o){o=b}function l(){this.length=0;return this}function d(){return this[0]}function g(){return this[this.length-1]}function i(){return this.select(function(u){return u!=null})}function t(){return this.inject([],function(v,u){if(Object.isArray(u)){return v.concat(u.flatten())}v.push(u);return v})}function h(){var u=m.call(arguments,0);return this.select(function(v){return !u.include(v)})}function f(u){return(u===false?this.toArray():this)._reverse()}function k(u){return this.inject([],function(x,w,v){if(0==v||(u?x.last()!=w:!x.include(w))){x.push(w)}return x})}function p(u){return this.uniq().findAll(function(v){return u.detect(function(w){return v===w})})}function q(){return m.call(this,0)}function j(){return this.length}function s(){return"["+this.map(Object.inspect).join(", ")+"]"}function a(w,u){u||(u=0);var v=this.length;if(u<0){u=v+u}for(;u<v;u++){if(this[u]===w){return u}}return -1}function n(v,u){u=isNaN(u)?this.length:(u<0?this.length+u:u)+1;var w=this.slice(0,u).reverse().indexOf(v);return(w<0)?w:u-w-1}function c(){var z=m.call(this,0),x;for(var v=0,w=arguments.length;v<w;v++){x=arguments[v];if(Object.isArray(x)&&!("callee" in x)){for(var u=0,y=x.length;u<y;u++){z.push(x[u])}}else{z.push(x)}}return z}Object.extend(r,Enumerable);if(!r._reverse){r._reverse=r.reverse}Object.extend(r,{_each:o,clear:l,first:d,last:g,compact:i,flatten:t,without:h,reverse:f,uniq:k,intersect:p,clone:q,toArray:q,size:j,inspect:s});var e=(function(){return[].concat(arguments)[0][0]!==1})(1,2);if(e){r.concat=c}if(!r.indexOf){r.indexOf=a}if(!r.lastIndexOf){r.lastIndexOf=n}})();function $H(a){return new Hash(a)}var Hash=Class.create(Enumerable,(function(){function e(p){this._object=Object.isHash(p)?p.toObject():Object.clone(p)}function f(q){for(var p in this._object){var r=this._object[p],s=[p,r];s.key=p;s.value=r;q(s)}}function j(p,q){return this._object[p]=q}function c(p){if(this._object[p]!==Object.prototype[p]){return this._object[p]}}function m(p){var q=this._object[p];delete this._object[p];return q}function o(){return Object.clone(this._object)}function n(){return this.pluck("key")}function l(){return this.pluck("value")}function g(q){var p=this.detect(function(r){return r.value===q});return p&&p.key}function i(p){return this.clone().update(p)}function d(p){return new Hash(p).inject(this,function(q,r){q.set(r.key,r.value);return q})}function b(p,q){if(Object.isUndefined(q)){return p}return p+"="+encodeURIComponent(String.interpret(q))}function a(){return this.inject([],function(t,w){var s=encodeURIComponent(w.key),q=w.value;if(q&&typeof q=="object"){if(Object.isArray(q)){var v=[];for(var r=0,p=q.length,u;r<p;r++){u=q[r];v.push(b(s,u))}return t.concat(v)}}else{t.push(b(s,q))}return t}).join("&")}function k(){return"#<Hash:{"+this.map(function(p){return p.map(Object.inspect).join(": ")}).join(", ")+"}>"}function h(){return new Hash(this)}return{initialize:e,_each:f,set:j,get:c,unset:m,toObject:o,toTemplateReplacements:o,keys:n,values:l,index:g,merge:i,update:d,toQueryString:a,inspect:k,toJSON:o,clone:h}})());Hash.from=$H;Object.extend(Number.prototype,(function(){function d(){return this.toPaddedString(2,16)}function b(){return this+1}function h(j,i){$R(0,this,true).each(j,i);return this}function g(k,j){var i=this.toString(j||10);return"0".times(k-i.length)+i}function a(){return Math.abs(this)}function c(){return Math.round(this)}function e(){return Math.ceil(this)}function f(){return Math.floor(this)}return{toColorPart:d,succ:b,times:h,toPaddedString:g,abs:a,round:c,ceil:e,floor:f}})());function $R(c,a,b){return new ObjectRange(c,a,b)}var ObjectRange=Class.create(Enumerable,(function(){function b(f,d,e){this.start=f;this.end=d;this.exclusive=e}function c(d){var e=this.start;while(this.include(e)){d(e);e=e.succ()}}function a(d){if(d<this.start){return false}if(this.exclusive){return d<this.end}return d<=this.end}return{initialize:b,_each:c,include:a}})());var Ajax={getTransport:function(){return Try.these(function(){return new XMLHttpRequest()},function(){return new ActiveXObject("Msxml2.XMLHTTP")},function(){return new ActiveXObject("Microsoft.XMLHTTP")})||false},activeRequestCount:0};Ajax.Responders={responders:[],_each:function(a){this.responders._each(a)},register:function(a){if(!this.include(a)){this.responders.push(a)}},unregister:function(a){this.responders=this.responders.without(a)},dispatch:function(d,b,c,a){this.each(function(f){if(Object.isFunction(f[d])){try{f[d].apply(f,[b,c,a])}catch(g){}}})}};Object.extend(Ajax.Responders,Enumerable);Ajax.Responders.register({onCreate:function(){Ajax.activeRequestCount++},onComplete:function(){Ajax.activeRequestCount--}});Ajax.Base=Class.create({initialize:function(a){this.options={method:"post",asynchronous:true,contentType:"application/x-www-form-urlencoded",encoding:"UTF-8",parameters:"",evalJSON:true,evalJS:true};Object.extend(this.options,a||{});this.options.method=this.options.method.toLowerCase();if(Object.isHash(this.options.parameters)){this.options.parameters=this.options.parameters.toObject()}}});Ajax.Request=Class.create(Ajax.Base,{_complete:false,initialize:function($super,b,a){$super(a);this.transport=Ajax.getTransport();this.request(b)},request:function(b){this.url=b;this.method=this.options.method;var d=Object.isString(this.options.parameters)?this.options.parameters:Object.toQueryString(this.options.parameters);if(!["get","post"].include(this.method)){d+=(d?"&":"")+"_method="+this.method;this.method="post"}if(d&&this.method==="get"){this.url+=(this.url.include("?")?"&":"?")+d}this.parameters=d.toQueryParams();try{var a=new Ajax.Response(this);if(this.options.onCreate){this.options.onCreate(a)}Ajax.Responders.dispatch("onCreate",this,a);this.transport.open(this.method.toUpperCase(),this.url,this.options.asynchronous);if(this.options.asynchronous){this.respondToReadyState.bind(this).defer(1)}this.transport.onreadystatechange=this.onStateChange.bind(this);this.setRequestHeaders();this.body=this.method=="post"?(this.options.postBody||d):null;this.transport.send(this.body);if(!this.options.asynchronous&&this.transport.overrideMimeType){this.onStateChange()}}catch(c){this.dispatchException(c)}},onStateChange:function(){var a=this.transport.readyState;if(a>1&&!((a==4)&&this._complete)){this.respondToReadyState(this.transport.readyState)}},setRequestHeaders:function(){var e={"X-Requested-With":"XMLHttpRequest","X-Prototype-Version":Prototype.Version,Accept:"text/javascript, text/html, application/xml, text/xml, */*"};if(this.method=="post"){e["Content-type"]=this.options.contentType+(this.options.encoding?"; charset="+this.options.encoding:"");if(this.transport.overrideMimeType&&(navigator.userAgent.match(/Gecko\/(\d{4})/)||[0,2005])[1]<2005){e.Connection="close"}}if(typeof this.options.requestHeaders=="object"){var c=this.options.requestHeaders;if(Object.isFunction(c.push)){for(var b=0,d=c.length;b<d;b+=2){e[c[b]]=c[b+1]}}else{$H(c).each(function(f){e[f.key]=f.value})}}for(var a in e){this.transport.setRequestHeader(a,e[a])}},success:function(){var a=this.getStatus();return !a||(a>=200&&a<300)||a==304},getStatus:function(){try{if(this.transport.status===1223){return 204}return this.transport.status||0}catch(a){return 0}},respondToReadyState:function(a){var c=Ajax.Request.Events[a],b=new Ajax.Response(this);if(c=="Complete"){try{this._complete=true;(this.options["on"+b.status]||this.options["on"+(this.success()?"Success":"Failure")]||Prototype.emptyFunction)(b,b.headerJSON)}catch(d){this.dispatchException(d)}var f=b.getHeader("Content-type");if(this.options.evalJS=="force"||(this.options.evalJS&&this.isSameOrigin()&&f&&f.match(/^\s*(text|application)\/(x-)?(java|ecma)script(;.*)?\s*$/i))){this.evalResponse()}}try{(this.options["on"+c]||Prototype.emptyFunction)(b,b.headerJSON);Ajax.Responders.dispatch("on"+c,this,b,b.headerJSON)}catch(d){this.dispatchException(d)}if(c=="Complete"){this.transport.onreadystatechange=Prototype.emptyFunction}},isSameOrigin:function(){var a=this.url.match(/^\s*https?:\/\/[^\/]*/);return !a||(a[0]=="#{protocol}//#{domain}#{port}".interpolate({protocol:location.protocol,domain:document.domain,port:location.port?":"+location.port:""}))},getHeader:function(a){try{return this.transport.getResponseHeader(a)||null}catch(b){return null}},evalResponse:function(){try{return eval((this.transport.responseText||"").unfilterJSON())}catch(e){this.dispatchException(e)}},dispatchException:function(a){(this.options.onException||Prototype.emptyFunction)(this,a);Ajax.Responders.dispatch("onException",this,a)}});Ajax.Request.Events=["Uninitialized","Loading","Loaded","Interactive","Complete"];Ajax.Response=Class.create({initialize:function(c){this.request=c;var d=this.transport=c.transport,a=this.readyState=d.readyState;if((a>2&&!Prototype.Browser.IE)||a==4){this.status=this.getStatus();this.statusText=this.getStatusText();this.responseText=String.interpret(d.responseText);this.headerJSON=this._getHeaderJSON()}if(a==4){var b=d.responseXML;this.responseXML=Object.isUndefined(b)?null:b;this.responseJSON=this._getResponseJSON()}},status:0,statusText:"",getStatus:Ajax.Request.prototype.getStatus,getStatusText:function(){try{return this.transport.statusText||""}catch(a){return""}},getHeader:Ajax.Request.prototype.getHeader,getAllHeaders:function(){try{return this.getAllResponseHeaders()}catch(a){return null}},getResponseHeader:function(a){return this.transport.getResponseHeader(a)},getAllResponseHeaders:function(){return this.transport.getAllResponseHeaders()},_getHeaderJSON:function(){var a=this.getHeader("X-JSON");if(!a){return null}a=decodeURIComponent(escape(a));try{return a.evalJSON(this.request.options.sanitizeJSON||!this.request.isSameOrigin())}catch(b){this.request.dispatchException(b)}},_getResponseJSON:function(){var a=this.request.options;if(!a.evalJSON||(a.evalJSON!="force"&&!(this.getHeader("Content-type")||"").include("application/json"))||this.responseText.blank()){return null}try{return this.responseText.evalJSON(a.sanitizeJSON||!this.request.isSameOrigin())}catch(b){this.request.dispatchException(b)}}});Ajax.Updater=Class.create(Ajax.Request,{initialize:function($super,a,c,b){this.container={success:(a.success||a),failure:(a.failure||(a.success?null:a))};b=Object.clone(b);var d=b.onComplete;b.onComplete=(function(e,f){this.updateContent(e.responseText);if(Object.isFunction(d)){d(e,f)}}).bind(this);$super(c,b)},updateContent:function(d){var c=this.container[this.success()?"success":"failure"],a=this.options;if(!a.evalScripts){d=d.stripScripts()}if(c=p$(c)){if(a.insertion){if(Object.isString(a.insertion)){var b={};b[a.insertion]=d;c.insert(b)}else{a.insertion(c,d)}}else{c.update(d)}}}});Ajax.PeriodicalUpdater=Class.create(Ajax.Base,{initialize:function($super,a,c,b){$super(b);this.onComplete=this.options.onComplete;this.frequency=(this.options.frequency||2);this.decay=(this.options.decay||1);this.updater={};this.container=a;this.url=c;this.start()},start:function(){this.options.onComplete=this.updateComplete.bind(this);this.onTimerEvent()},stop:function(){this.updater.options.onComplete=undefined;clearTimeout(this.timer);(this.onComplete||Prototype.emptyFunction).apply(this,arguments)},updateComplete:function(a){if(this.options.decay){this.decay=(a.responseText==this.lastText?this.decay*this.options.decay:1);this.lastText=a.responseText}this.timer=this.onTimerEvent.bind(this).delay(this.decay*this.frequency)},onTimerEvent:function(){this.updater=new Ajax.Updater(this.container,this.url,this.options)}});function p$(b){if(arguments.length>1){for(var a=0,d=[],c=arguments.length;a<c;a++){d.push(p$(arguments[a]))}return d}if(Object.isString(b)){b=document.getElementById(b)}return Element.extend(b)}if(Prototype.BrowserFeatures.XPath){document._getElementsByXPath=function(f,a){var c=[];var e=document.evaluate(f,p$(a)||document,null,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,null);for(var b=0,d=e.snapshotLength;b<d;b++){c.push(Element.extend(e.snapshotItem(b)))}return c}}if(!Node){var Node={}}if(!Node.ELEMENT_NODE){Object.extend(Node,{ELEMENT_NODE:1,ATTRIBUTE_NODE:2,TEXT_NODE:3,CDATA_SECTION_NODE:4,ENTITY_REFERENCE_NODE:5,ENTITY_NODE:6,PROCESSING_INSTRUCTION_NODE:7,COMMENT_NODE:8,DOCUMENT_NODE:9,DOCUMENT_TYPE_NODE:10,DOCUMENT_FRAGMENT_NODE:11,NOTATION_NODE:12})}(function(c){function d(f,e){if(f==="select"){return false}if("type" in e){return false}return true}var b=(function(){try{var e=document.createElement('<input name="x">');return e.tagName.toLowerCase()==="input"&&e.name==="x"}catch(f){return false}})();var a=c.Element;c.Element=function(g,f){f=f||{};g=g.toLowerCase();var e=Element.cache;if(b&&f.name){g="<"+g+' name="'+f.name+'">';delete f.name;return Element.writeAttribute(document.createElement(g),f)}if(!e[g]){e[g]=Element.extend(document.createElement(g))}var h=d(g,f)?e[g].cloneNode(false):document.createElement(g);return Element.writeAttribute(h,f)};Object.extend(c.Element,a||{});if(a){c.Element.prototype=a.prototype}})(this);Element.idCounter=1;Element.cache={};Element._purgeElement=function(b){var a=b._prototypeUID;if(a){Element.stopObserving(b);b._prototypeUID=void 0;delete Element.Storage[a]}};Element.Methods={visible:function(a){return p$(a).style.display!="none"},toggle:function(a){a=p$(a);Element[Element.visible(a)?"hide":"show"](a);return a},hide:function(a){a=p$(a);a.style.display="none";return a},show:function(a){a=p$(a);a.style.display="";return a},remove:function(a){a=p$(a);a.parentNode.removeChild(a);return a},update:(function(){var d=(function(){var g=document.createElement("select"),h=true;g.innerHTML='<option value="test">test</option>';if(g.options&&g.options[0]){h=g.options[0].nodeName.toUpperCase()!=="OPTION"}g=null;return h})();var b=(function(){try{var g=document.createElement("table");if(g&&g.tBodies){g.innerHTML="<tbody><tr><td>test</td></tr></tbody>";var i=typeof g.tBodies[0]=="undefined";g=null;return i}}catch(h){return true}})();var a=(function(){try{var g=document.createElement("div");g.innerHTML="<link>";var i=(g.childNodes.length===0);g=null;return i}catch(h){return true}})();var c=d||b||a;var f=(function(){var g=document.createElement("script"),i=false;try{g.appendChild(document.createTextNode(""));i=!g.firstChild||g.firstChild&&g.firstChild.nodeType!==3}catch(h){i=true}g=null;return i})();function e(l,m){l=p$(l);var g=Element._purgeElement;var n=l.getElementsByTagName("*"),k=n.length;while(k--){g(n[k])}if(m&&m.toElement){m=m.toElement()}if(Object.isElement(m)){return l.update().insert(m)}m=Object.toHTML(m);var j=l.tagName.toUpperCase();if(j==="SCRIPT"&&f){l.text=m;return l}if(c){if(j in Element._insertionTranslations.tags){while(l.firstChild){l.removeChild(l.firstChild)}Element._getContentFromAnonymousElement(j,m.stripScripts()).each(function(i){l.appendChild(i)})}else{if(a&&Object.isString(m)&&m.indexOf("<link")>-1){while(l.firstChild){l.removeChild(l.firstChild)}var h=Element._getContentFromAnonymousElement(j,m.stripScripts(),true);h.each(function(i){l.appendChild(i)})}else{l.innerHTML=m.stripScripts()}}}else{l.innerHTML=m.stripScripts()}m.evalScripts.bind(m).defer();return l}return e})(),replace:function(b,c){b=p$(b);if(c&&c.toElement){c=c.toElement()}else{if(!Object.isElement(c)){c=Object.toHTML(c);var a=b.ownerDocument.createRange();a.selectNode(b);c.evalScripts.bind(c).defer();c=a.createContextualFragment(c.stripScripts())}}b.parentNode.replaceChild(c,b);return b},insert:function(c,e){c=p$(c);if(Object.isString(e)||Object.isNumber(e)||Object.isElement(e)||(e&&(e.toElement||e.toHTML))){e={bottom:e}}var d,f,b,g;for(var a in e){d=e[a];a=a.toLowerCase();f=Element._insertionTranslations[a];if(d&&d.toElement){d=d.toElement()}if(Object.isElement(d)){f(c,d);continue}d=Object.toHTML(d);b=((a=="before"||a=="after")?c.parentNode:c).tagName.toUpperCase();g=Element._getContentFromAnonymousElement(b,d.stripScripts());if(a=="top"||a=="after"){g.reverse()}g.each(f.curry(c));d.evalScripts.bind(d).defer()}return c},wrap:function(b,c,a){b=p$(b);if(Object.isElement(c)){p$(c).writeAttribute(a||{})}else{if(Object.isString(c)){c=new Element(c,a)}else{c=new Element("div",c)}}if(b.parentNode){b.parentNode.replaceChild(c,b)}c.appendChild(b);return c},inspect:function(b){b=p$(b);var a="<"+b.tagName.toLowerCase();$H({id:"id",className:"class"}).each(function(f){var e=f.first(),c=f.last(),d=(b[e]||"").toString();if(d){a+=" "+c+"="+d.inspect(true)}});return a+">"},recursivelyCollect:function(a,c,d){a=p$(a);d=d||-1;var b=[];while(a=a[c]){if(a.nodeType==1){b.push(Element.extend(a))}if(b.length==d){break}}return b},ancestors:function(a){return Element.recursivelyCollect(a,"parentNode")},descendants:function(a){return Element.select(a,"*")},firstDescendant:function(a){a=p$(a).firstChild;while(a&&a.nodeType!=1){a=a.nextSibling}return p$(a)},immediateDescendants:function(b){var a=[],c=p$(b).firstChild;while(c){if(c.nodeType===1){a.push(Element.extend(c))}c=c.nextSibling}return a},previousSiblings:function(a,b){return Element.recursivelyCollect(a,"previousSibling")},nextSiblings:function(a){return Element.recursivelyCollect(a,"nextSibling")},siblings:function(a){a=p$(a);return Element.previousSiblings(a).reverse().concat(Element.nextSiblings(a))},match:function(b,a){b=p$(b);if(Object.isString(a)){return Prototype.Selector.match(b,a)}return a.match(b)},up:function(b,d,a){b=p$(b);if(arguments.length==1){return p$(b.parentNode)}var c=Element.ancestors(b);return Object.isNumber(d)?c[d]:Prototype.Selector.find(c,d,a)},down:function(b,c,a){b=p$(b);if(arguments.length==1){return Element.firstDescendant(b)}return Object.isNumber(c)?Element.descendants(b)[c]:Element.select(b,c)[a||0]},previous:function(b,c,a){b=p$(b);if(Object.isNumber(c)){a=c,c=false}if(!Object.isNumber(a)){a=0}if(c){return Prototype.Selector.find(b.previousSiblings(),c,a)}else{return b.recursivelyCollect("previousSibling",a+1)[a]}},next:function(b,d,a){b=p$(b);if(Object.isNumber(d)){a=d,d=false}if(!Object.isNumber(a)){a=0}if(d){return Prototype.Selector.find(b.nextSiblings(),d,a)}else{var c=Object.isNumber(a)?a+1:1;return b.recursivelyCollect("nextSibling",a+1)[a]}},select:function(a){a=p$(a);var b=Array.prototype.slice.call(arguments,1).join(", ");return Prototype.Selector.select(b,a)},adjacent:function(a){a=p$(a);var b=Array.prototype.slice.call(arguments,1).join(", ");return Prototype.Selector.select(b,a.parentNode).without(a)},identify:function(a){a=p$(a);var b=Element.readAttribute(a,"id");if(b){return b}do{b="anonymous_element_"+Element.idCounter++}while(p$(b));Element.writeAttribute(a,"id",b);return b},readAttribute:function(c,a){c=p$(c);if(Prototype.Browser.IE){var b=Element._attributeTranslations.read;if(b.values[a]){return b.values[a](c,a)}if(b.names[a]){a=b.names[a]}if(a.include(":")){return(!c.attributes||!c.attributes[a])?null:c.attributes[a].value}}return c.getAttribute(a)},writeAttribute:function(e,c,f){e=p$(e);var b={},d=Element._attributeTranslations.write;if(typeof c=="object"){b=c}else{b[c]=Object.isUndefined(f)?true:f}for(var a in b){c=d.names[a]||a;f=b[a];if(d.values[a]){c=d.values[a](e,f)}if(f===false||f===null){e.removeAttribute(c)}else{if(f===true){e.setAttribute(c,c)}else{e.setAttribute(c,f)}}}return e},getHeight:function(a){return Element.getDimensions(a).height},getWidth:function(a){return Element.getDimensions(a).width},classNames:function(a){return new Element.ClassNames(a)},hasClassName:function(a,b){if(!(a=p$(a))){return}var c=a.className;return(c.length>0&&(c==b||new RegExp("(^|\\s)"+b+"(\\s|$)").test(c)))},addClassName:function(a,b){if(!(a=p$(a))){return}if(!Element.hasClassName(a,b)){a.className+=(a.className?" ":"")+b}return a},removeClassName:function(a,b){if(!(a=p$(a))){return}a.className=a.className.replace(new RegExp("(^|\\s+)"+b+"(\\s+|$)")," ").strip();return a},toggleClassName:function(a,b){if(!(a=p$(a))){return}return Element[Element.hasClassName(a,b)?"removeClassName":"addClassName"](a,b)},cleanWhitespace:function(b){b=p$(b);var c=b.firstChild;while(c){var a=c.nextSibling;if(c.nodeType==3&&!/\S/.test(c.nodeValue)){b.removeChild(c)}c=a}return b},empty:function(a){return p$(a).innerHTML.blank()},descendantOf:function(b,a){b=p$(b),a=p$(a);if(b.compareDocumentPosition){return(b.compareDocumentPosition(a)&8)===8}if(a.contains){return a.contains(b)&&a!==b}while(b=b.parentNode){if(b==a){return true}}return false},scrollTo:function(a){a=p$(a);var b=Element.cumulativeOffset(a);window.scrollTo(b[0],b[1]);return a},getStyle:function(b,c){b=p$(b);c=c=="float"?"cssFloat":c.camelize();var d=b.style[c];if(!d||d=="auto"){var a=document.defaultView.getComputedStyle(b,null);d=a?a[c]:null}if(c=="opacity"){return d?parseFloat(d):1}return d=="auto"?null:d},getOpacity:function(a){return p$(a).getStyle("opacity")},setStyle:function(b,c){b=p$(b);var e=b.style,a;if(Object.isString(c)){b.style.cssText+=";"+c;return c.include("opacity")?b.setOpacity(c.match(/opacity:\s*(\d?\.?\d*)/)[1]):b}for(var d in c){if(d=="opacity"){b.setOpacity(c[d])}else{e[(d=="float"||d=="cssFloat")?(Object.isUndefined(e.styleFloat)?"cssFloat":"styleFloat"):d]=c[d]}}return b},setOpacity:function(a,b){a=p$(a);a.style.opacity=(b==1||b==="")?"":(b<0.00001)?0:b;return a},makePositioned:function(a){a=p$(a);var b=Element.getStyle(a,"position");if(b=="static"||!b){a._madePositioned=true;a.style.position="relative";if(Prototype.Browser.Opera){a.style.top=0;a.style.left=0}}return a},undoPositioned:function(a){a=p$(a);if(a._madePositioned){a._madePositioned=undefined;a.style.position=a.style.top=a.style.left=a.style.bottom=a.style.right=""}return a},makeClipping:function(a){a=p$(a);if(a._overflow){return a}a._overflow=Element.getStyle(a,"overflow")||"auto";if(a._overflow!=="hidden"){a.style.overflow="hidden"}return a},undoClipping:function(a){a=p$(a);if(!a._overflow){return a}a.style.overflow=a._overflow=="auto"?"":a._overflow;a._overflow=null;return a},clonePosition:function(b,d){var a=Object.extend({setLeft:true,setTop:true,setWidth:true,setHeight:true,offsetTop:0,offsetLeft:0},arguments[2]||{});d=p$(d);var e=Element.viewportOffset(d),f=[0,0],c=null;b=p$(b);if(Element.getStyle(b,"position")=="absolute"){c=Element.getOffsetParent(b);f=Element.viewportOffset(c)}if(c==document.body){f[0]-=document.body.offsetLeft;f[1]-=document.body.offsetTop}if(a.setLeft){b.style.left=(e[0]-f[0]+a.offsetLeft)+"px"}if(a.setTop){b.style.top=(e[1]-f[1]+a.offsetTop)+"px"}if(a.setWidth){b.style.width=d.offsetWidth+"px"}if(a.setHeight){b.style.height=d.offsetHeight+"px"}return b}};Object.extend(Element.Methods,{getElementsBySelector:Element.Methods.select,childElements:Element.Methods.immediateDescendants});Element._attributeTranslations={write:{names:{className:"class",htmlFor:"for"},values:{}}};if(Prototype.Browser.Opera){Element.Methods.getStyle=Element.Methods.getStyle.wrap(function(d,b,c){switch(c){case"height":case"width":if(!Element.visible(b)){return null}var e=parseInt(d(b,c),10);if(e!==b["offset"+c.capitalize()]){return e+"px"}var a;if(c==="height"){a=["border-top-width","padding-top","padding-bottom","border-bottom-width"]}else{a=["border-left-width","padding-left","padding-right","border-right-width"]}return a.inject(e,function(f,g){var h=d(b,g);return h===null?f:f-parseInt(h,10)})+"px";default:return d(b,c)}});Element.Methods.readAttribute=Element.Methods.readAttribute.wrap(function(c,a,b){if(b==="title"){return a.title}return c(a,b)})}else{if(Prototype.Browser.IE){Element.Methods.getStyle=function(a,b){a=p$(a);b=(b=="float"||b=="cssFloat")?"styleFloat":b.camelize();var c=a.style[b];if(!c&&a.currentStyle){c=a.currentStyle[b]}if(b=="opacity"){if(c=(a.getStyle("filter")||"").match(/alpha\(opacity=(.*)\)/)){if(c[1]){return parseFloat(c[1])/100}}return 1}if(c=="auto"){if((b=="width"||b=="height")&&(a.getStyle("display")!="none")){return a["offset"+b.capitalize()]+"px"}return null}return c};Element.Methods.setOpacity=function(b,e){function f(g){return g.replace(/alpha\([^\)]*\)/gi,"")}b=p$(b);var a=b.currentStyle;if((a&&!a.hasLayout)||(!a&&b.style.zoom=="normal")){b.style.zoom=1}var d=b.getStyle("filter"),c=b.style;if(e==1||e===""){(d=f(d))?c.filter=d:c.removeAttribute("filter");return b}else{if(e<0.00001){e=0}}c.filter=f(d)+"alpha(opacity="+(e*100)+")";return b};Element._attributeTranslations=(function(){var b="className",a="for",c=document.createElement("div");c.setAttribute(b,"x");if(c.className!=="x"){c.setAttribute("class","x");if(c.className==="x"){b="class"}}c=null;c=document.createElement("label");c.setAttribute(a,"x");if(c.htmlFor!=="x"){c.setAttribute("htmlFor","x");if(c.htmlFor==="x"){a="htmlFor"}}c=null;return{read:{names:{"class":b,className:b,"for":a,htmlFor:a},values:{_getAttr:function(d,e){return d.getAttribute(e)},_getAttr2:function(d,e){return d.getAttribute(e,2)},_getAttrNode:function(d,f){var e=d.getAttributeNode(f);return e?e.value:""},_getEv:(function(){var d=document.createElement("div"),g;d.onclick=Prototype.emptyFunction;var e=d.getAttribute("onclick");if(String(e).indexOf("{")>-1){g=function(f,h){h=f.getAttribute(h);if(!h){return null}h=h.toString();h=h.split("{")[1];h=h.split("}")[0];return h.strip()}}else{if(e===""){g=function(f,h){h=f.getAttribute(h);if(!h){return null}return h.strip()}}}d=null;return g})(),_flag:function(d,e){return p$(d).hasAttribute(e)?e:null},style:function(d){return d.style.cssText.toLowerCase()},title:function(d){return d.title}}}}})();Element._attributeTranslations.write={names:Object.extend({cellpadding:"cellPadding",cellspacing:"cellSpacing"},Element._attributeTranslations.read.names),values:{checked:function(a,b){a.checked=!!b},style:function(a,b){a.style.cssText=b?b:""}}};Element._attributeTranslations.has={};$w("colSpan rowSpan vAlign dateTime accessKey tabIndex encType maxLength readOnly longDesc frameBorder").each(function(a){Element._attributeTranslations.write.names[a.toLowerCase()]=a;Element._attributeTranslations.has[a.toLowerCase()]=a});(function(a){Object.extend(a,{href:a._getAttr2,src:a._getAttr2,type:a._getAttr,action:a._getAttrNode,disabled:a._flag,checked:a._flag,readonly:a._flag,multiple:a._flag,onload:a._getEv,onunload:a._getEv,onclick:a._getEv,ondblclick:a._getEv,onmousedown:a._getEv,onmouseup:a._getEv,onmouseover:a._getEv,onmousemove:a._getEv,onmouseout:a._getEv,onfocus:a._getEv,onblur:a._getEv,onkeypress:a._getEv,onkeydown:a._getEv,onkeyup:a._getEv,onsubmit:a._getEv,onreset:a._getEv,onselect:a._getEv,onchange:a._getEv})})(Element._attributeTranslations.read.values);if(Prototype.BrowserFeatures.ElementExtensions){(function(){function a(e){var b=e.getElementsByTagName("*"),d=[];for(var c=0,f;f=b[c];c++){if(f.tagName!=="!"){d.push(f)}}return d}Element.Methods.down=function(c,d,b){c=p$(c);if(arguments.length==1){return c.firstDescendant()}return Object.isNumber(d)?a(c)[d]:Element.select(c,d)[b||0]}})()}}else{if(Prototype.Browser.Gecko&&/rv:1\.8\.0/.test(navigator.userAgent)){Element.Methods.setOpacity=function(a,b){a=p$(a);a.style.opacity=(b==1)?0.999999:(b==="")?"":(b<0.00001)?0:b;return a}}else{if(Prototype.Browser.WebKit){Element.Methods.setOpacity=function(a,b){a=p$(a);a.style.opacity=(b==1||b==="")?"":(b<0.00001)?0:b;if(b==1){if(a.tagName.toUpperCase()=="IMG"&&a.width){a.width++;a.width--}else{try{var d=document.createTextNode(" ");a.appendChild(d);a.removeChild(d)}catch(c){}}}return a}}}}}if("outerHTML" in document.documentElement){Element.Methods.replace=function(c,e){c=p$(c);if(e&&e.toElement){e=e.toElement()}if(Object.isElement(e)){c.parentNode.replaceChild(e,c);return c}e=Object.toHTML(e);var d=c.parentNode,b=d.tagName.toUpperCase();if(Element._insertionTranslations.tags[b]){var f=c.next(),a=Element._getContentFromAnonymousElement(b,e.stripScripts());d.removeChild(c);if(f){a.each(function(g){d.insertBefore(g,f)})}else{a.each(function(g){d.appendChild(g)})}}else{c.outerHTML=e.stripScripts()}e.evalScripts.bind(e).defer();return c}}Element._returnOffset=function(b,c){var a=[b,c];a.left=b;a.top=c;return a};Element._getContentFromAnonymousElement=function(e,d,f){var g=new Element("div"),c=Element._insertionTranslations.tags[e];var a=false;if(c){a=true}else{if(f){a=true;c=["","",0]}}if(a){g.innerHTML="&nbsp;"+c[0]+d+c[1];g.removeChild(g.firstChild);for(var b=c[2];b--;){g=g.firstChild}}else{g.innerHTML=d}return $A(g.childNodes)};Element._insertionTranslations={before:function(a,b){a.parentNode.insertBefore(b,a)},top:function(a,b){a.insertBefore(b,a.firstChild)},bottom:function(a,b){a.appendChild(b)},after:function(a,b){a.parentNode.insertBefore(b,a.nextSibling)},tags:{TABLE:["<table>","</table>",1],TBODY:["<table><tbody>","</tbody></table>",2],TR:["<table><tbody><tr>","</tr></tbody></table>",3],TD:["<table><tbody><tr><td>","</td></tr></tbody></table>",4],SELECT:["<select>","</select>",1]}};(function(){var a=Element._insertionTranslations.tags;Object.extend(a,{THEAD:a.TBODY,TFOOT:a.TBODY,TH:a.TD})})();Element.Methods.Simulated={hasAttribute:function(a,c){c=Element._attributeTranslations.has[c]||c;var b=p$(a).getAttributeNode(c);return !!(b&&b.specified)}};Element.Methods.ByTag={};Object.extend(Element,Element.Methods);(function(a){if(!Prototype.BrowserFeatures.ElementExtensions&&a.__proto__){window.HTMLElement={};window.HTMLElement.prototype=a.__proto__;Prototype.BrowserFeatures.ElementExtensions=true}a=null})(document.createElement("div"));Element.extend=(function(){function c(g){if(typeof window.Element!="undefined"){var i=window.Element.prototype;if(i){var k="_"+(Math.random()+"").slice(2),h=document.createElement(g);i[k]="x";var j=(h[k]!=="x");delete i[k];h=null;return j}}return false}function b(h,g){for(var j in g){var i=g[j];if(Object.isFunction(i)&&!(j in h)){h[j]=i.methodize()}}}var d=c("object");if(Prototype.BrowserFeatures.SpecificElementExtensions){if(d){return function(h){if(h&&typeof h._extendedByPrototype=="undefined"){var g=h.tagName;if(g&&(/^(?:object|applet|embed)$/i.test(g))){b(h,Element.Methods);b(h,Element.Methods.Simulated);b(h,Element.Methods.ByTag[g.toUpperCase()])}}return h}}return Prototype.K}var a={},e=Element.Methods.ByTag;var f=Object.extend(function(i){if(!i||typeof i._extendedByPrototype!="undefined"||i.nodeType!=1||i==window){return i}var g=Object.clone(a),h=i.tagName.toUpperCase();if(e[h]){Object.extend(g,e[h])}b(i,g);i._extendedByPrototype=Prototype.emptyFunction;return i},{refresh:function(){if(!Prototype.BrowserFeatures.ElementExtensions){Object.extend(a,Element.Methods);Object.extend(a,Element.Methods.Simulated)}}});f.refresh();return f})();if(document.documentElement.hasAttribute){Element.hasAttribute=function(a,b){return a.hasAttribute(b)}}else{Element.hasAttribute=Element.Methods.Simulated.hasAttribute}Element.addMethods=function(c){var i=Prototype.BrowserFeatures,d=Element.Methods.ByTag;if(!c){Object.extend(Form,Form.Methods);Object.extend(Form.Element,Form.Element.Methods);Object.extend(Element.Methods.ByTag,{FORM:Object.clone(Form.Methods),INPUT:Object.clone(Form.Element.Methods),SELECT:Object.clone(Form.Element.Methods),TEXTAREA:Object.clone(Form.Element.Methods),BUTTON:Object.clone(Form.Element.Methods)})}if(arguments.length==2){var b=c;c=arguments[1]}if(!b){Object.extend(Element.Methods,c||{})}else{if(Object.isArray(b)){b.each(g)}else{g(b)}}function g(k){k=k.toUpperCase();if(!Element.Methods.ByTag[k]){Element.Methods.ByTag[k]={}}Object.extend(Element.Methods.ByTag[k],c)}function a(m,l,k){k=k||false;for(var o in m){var n=m[o];if(!Object.isFunction(n)){continue}if(!k||!(o in l)){l[o]=n.methodize()}}}function e(n){var k;var m={OPTGROUP:"OptGroup",TEXTAREA:"TextArea",P:"Paragraph",FIELDSET:"FieldSet",UL:"UList",OL:"OList",DL:"DList",DIR:"Directory",H1:"Heading",H2:"Heading",H3:"Heading",H4:"Heading",H5:"Heading",H6:"Heading",Q:"Quote",INS:"Mod",DEL:"Mod",A:"Anchor",IMG:"Image",CAPTION:"TableCaption",COL:"TableCol",COLGROUP:"TableCol",THEAD:"TableSection",TFOOT:"TableSection",TBODY:"TableSection",TR:"TableRow",TH:"TableCell",TD:"TableCell",FRAMESET:"FrameSet",IFRAME:"IFrame"};if(m[n]){k="HTML"+m[n]+"Element"}if(window[k]){return window[k]}k="HTML"+n+"Element";if(window[k]){return window[k]}k="HTML"+n.capitalize()+"Element";if(window[k]){return window[k]}var l=document.createElement(n),o=l.__proto__||l.constructor.prototype;l=null;return o}var h=window.HTMLElement?HTMLElement.prototype:Element.prototype;if(i.ElementExtensions){a(Element.Methods,h);a(Element.Methods.Simulated,h,true)}if(i.SpecificElementExtensions){for(var j in Element.Methods.ByTag){var f=e(j);if(Object.isUndefined(f)){continue}a(d[j],f.prototype)}}Object.extend(Element,Element.Methods);delete Element.ByTag;if(Element.extend.refresh){Element.extend.refresh()}Element.cache={}};document.viewport={getDimensions:function(){return{width:this.getWidth(),height:this.getHeight()}},getScrollOffsets:function(){return Element._returnOffset(window.pageXOffset||document.documentElement.scrollLeft||document.body.scrollLeft,window.pageYOffset||document.documentElement.scrollTop||document.body.scrollTop)}};(function(b){var g=Prototype.Browser,e=document,c,d={};function a(){if(g.WebKit&&!e.evaluate){return document}if(g.Opera&&window.parseFloat(window.opera.version())<9.5){return document.body}return document.documentElement}function f(h){if(!c){c=a()}d[h]="client"+h;b["get"+h]=function(){return c[d[h]]};return b["get"+h]()}b.getWidth=f.curry("Width");b.getHeight=f.curry("Height")})(document.viewport);Element.Storage={UID:1};Element.addMethods({getStorage:function(b){if(!(b=p$(b))){return}var a;if(b===window){a=0}else{if(typeof b._prototypeUID==="undefined"){b._prototypeUID=Element.Storage.UID++}a=b._prototypeUID}if(!Element.Storage[a]){Element.Storage[a]=$H()}return Element.Storage[a]},store:function(b,a,c){if(!(b=p$(b))){return}if(arguments.length===2){Element.getStorage(b).update(a)}else{Element.getStorage(b).set(a,c)}return b},retrieve:function(c,b,a){if(!(c=p$(c))){return}var e=Element.getStorage(c),d=e.get(b);if(Object.isUndefined(d)){e.set(b,a);d=a}return d},clone:function(c,a){if(!(c=p$(c))){return}var e=c.cloneNode(a);e._prototypeUID=void 0;if(a){var d=Element.select(e,"*"),b=d.length;while(b--){d[b]._prototypeUID=void 0}}return Element.extend(e)},purge:function(c){if(!(c=p$(c))){return}var a=Element._purgeElement;a(c);var d=c.getElementsByTagName("*"),b=d.length;while(b--){a(d[b])}return null}});(function(){function h(v){var u=v.match(/^(\d+)%?$/i);if(!u){return null}return(Number(u[1])/100)}function o(F,G,v){var y=null;if(Object.isElement(F)){y=F;F=y.getStyle(G)}if(F===null){return null}if((/^(?:-)?\d+(\.\d+)?(px)?$/i).test(F)){return window.parseFloat(F)}var A=F.include("%"),w=(v===document.viewport);if(/\d/.test(F)&&y&&y.runtimeStyle&&!(A&&w)){var u=y.style.left,E=y.runtimeStyle.left;y.runtimeStyle.left=y.currentStyle.left;y.style.left=F||0;F=y.style.pixelLeft;y.style.left=u;y.runtimeStyle.left=E;return F}if(y&&A){v=v||y.parentNode;var x=h(F);var B=null;var z=y.getStyle("position");var D=G.include("left")||G.include("right")||G.include("width");var C=G.include("top")||G.include("bottom")||G.include("height");if(v===document.viewport){if(D){B=document.viewport.getWidth()}else{if(C){B=document.viewport.getHeight()}}}else{if(D){B=p$(v).measure("width")}else{if(C){B=p$(v).measure("height")}}}return(B===null)?0:B*x}return 0}function g(u){if(Object.isString(u)&&u.endsWith("px")){return u}return u+"px"}function j(v){var u=v;while(v&&v.parentNode){var w=v.getStyle("display");if(w==="none"){return false}v=p$(v.parentNode)}return true}var d=Prototype.K;if("currentStyle" in document.documentElement){d=function(u){if(!u.currentStyle.hasLayout){u.style.zoom=1}return u}}function f(u){if(u.include("border")){u=u+"-width"}return u.camelize()}Element.Layout=Class.create(Hash,{initialize:function($super,v,u){$super();this.element=p$(v);Element.Layout.PROPERTIES.each(function(w){this._set(w,null)},this);if(u){this._preComputing=true;this._begin();Element.Layout.PROPERTIES.each(this._compute,this);this._end();this._preComputing=false}},_set:function(v,u){return Hash.prototype.set.call(this,v,u)},set:function(v,u){throw"Properties of Element.Layout are read-only."},get:function($super,v){var u=$super(v);return u===null?this._compute(v):u},_begin:function(){if(this._prepared){return}var y=this.element;if(j(y)){this._prepared=true;return}var A={position:y.style.position||"",width:y.style.width||"",visibility:y.style.visibility||"",display:y.style.display||""};y.store("prototype_original_styles",A);var B=y.getStyle("position"),u=y.getStyle("width");if(u==="0px"||u===null){y.style.display="block";u=y.getStyle("width")}var v=(B==="fixed")?document.viewport:y.parentNode;y.setStyle({position:"absolute",visibility:"hidden",display:"block"});var w=y.getStyle("width");var x;if(u&&(w===u)){x=o(y,"width",v)}else{if(B==="absolute"||B==="fixed"){x=o(y,"width",v)}else{var C=y.parentNode,z=p$(C).getLayout();x=z.get("width")-this.get("margin-left")-this.get("border-left")-this.get("padding-left")-this.get("padding-right")-this.get("border-right")-this.get("margin-right")}}y.setStyle({width:x+"px"});this._prepared=true},_end:function(){var v=this.element;var u=v.retrieve("prototype_original_styles");v.store("prototype_original_styles",null);v.setStyle(u);this._prepared=false},_compute:function(v){var u=Element.Layout.COMPUTATIONS;if(!(v in u)){throw"Property not found."}return this._set(v,u[v].call(this,this.element))},toObject:function(){var u=$A(arguments);var v=(u.length===0)?Element.Layout.PROPERTIES:u.join(" ").split(" ");var w={};v.each(function(x){if(!Element.Layout.PROPERTIES.include(x)){return}var y=this.get(x);if(y!=null){w[x]=y}},this);return w},toHash:function(){var u=this.toObject.apply(this,arguments);return new Hash(u)},toCSS:function(){var u=$A(arguments);var w=(u.length===0)?Element.Layout.PROPERTIES:u.join(" ").split(" ");var v={};w.each(function(x){if(!Element.Layout.PROPERTIES.include(x)){return}if(Element.Layout.COMPOSITE_PROPERTIES.include(x)){return}var y=this.get(x);if(y!=null){v[f(x)]=y+"px"}},this);return v},inspect:function(){return"#<Element.Layout>"}});Object.extend(Element.Layout,{PROPERTIES:$w("height width top left right bottom border-left border-right border-top border-bottom padding-left padding-right padding-top padding-bottom margin-top margin-bottom margin-left margin-right padding-box-width padding-box-height border-box-width border-box-height margin-box-width margin-box-height"),COMPOSITE_PROPERTIES:$w("padding-box-width padding-box-height margin-box-width margin-box-height border-box-width border-box-height"),COMPUTATIONS:{height:function(w){if(!this._preComputing){this._begin()}var u=this.get("border-box-height");if(u<=0){if(!this._preComputing){this._end()}return 0}var x=this.get("border-top"),v=this.get("border-bottom");var z=this.get("padding-top"),y=this.get("padding-bottom");if(!this._preComputing){this._end()}return u-x-v-z-y},width:function(w){if(!this._preComputing){this._begin()}var v=this.get("border-box-width");if(v<=0){if(!this._preComputing){this._end()}return 0}var z=this.get("border-left"),u=this.get("border-right");var x=this.get("padding-left"),y=this.get("padding-right");if(!this._preComputing){this._end()}return v-z-u-x-y},"padding-box-height":function(v){var u=this.get("height"),x=this.get("padding-top"),w=this.get("padding-bottom");return u+x+w},"padding-box-width":function(u){var v=this.get("width"),w=this.get("padding-left"),x=this.get("padding-right");return v+w+x},"border-box-height":function(v){if(!this._preComputing){this._begin()}var u=v.offsetHeight;if(!this._preComputing){this._end()}return u},"border-box-width":function(u){if(!this._preComputing){this._begin()}var v=u.offsetWidth;if(!this._preComputing){this._end()}return v},"margin-box-height":function(v){var u=this.get("border-box-height"),w=this.get("margin-top"),x=this.get("margin-bottom");if(u<=0){return 0}return u+w+x},"margin-box-width":function(w){var v=this.get("border-box-width"),x=this.get("margin-left"),u=this.get("margin-right");if(v<=0){return 0}return v+x+u},top:function(u){var v=u.positionedOffset();return v.top},bottom:function(u){var x=u.positionedOffset(),v=u.getOffsetParent(),w=v.measure("height");var y=this.get("border-box-height");return w-y-x.top},left:function(u){var v=u.positionedOffset();return v.left},right:function(w){var y=w.positionedOffset(),x=w.getOffsetParent(),u=x.measure("width");var v=this.get("border-box-width");return u-v-y.left},"padding-top":function(u){return o(u,"paddingTop")},"padding-bottom":function(u){return o(u,"paddingBottom")},"padding-left":function(u){return o(u,"paddingLeft")},"padding-right":function(u){return o(u,"paddingRight")},"border-top":function(u){return o(u,"borderTopWidth")},"border-bottom":function(u){return o(u,"borderBottomWidth")},"border-left":function(u){return o(u,"borderLeftWidth")},"border-right":function(u){return o(u,"borderRightWidth")},"margin-top":function(u){return o(u,"marginTop")},"margin-bottom":function(u){return o(u,"marginBottom")},"margin-left":function(u){return o(u,"marginLeft")},"margin-right":function(u){return o(u,"marginRight")}}});if("getBoundingClientRect" in document.documentElement){Object.extend(Element.Layout.COMPUTATIONS,{right:function(v){var w=d(v.getOffsetParent());var x=v.getBoundingClientRect(),u=w.getBoundingClientRect();return(u.right-x.right).round()},bottom:function(v){var w=d(v.getOffsetParent());var x=v.getBoundingClientRect(),u=w.getBoundingClientRect();return(u.bottom-x.bottom).round()}})}Element.Offset=Class.create({initialize:function(v,u){this.left=v.round();this.top=u.round();this[0]=this.left;this[1]=this.top},relativeTo:function(u){return new Element.Offset(this.left-u.left,this.top-u.top)},inspect:function(){return"#<Element.Offset left: #{left} top: #{top}>".interpolate(this)},toString:function(){return"[#{left}, #{top}]".interpolate(this)},toArray:function(){return[this.left,this.top]}});function r(v,u){return new Element.Layout(v,u)}function b(u,v){return p$(u).getLayout().get(v)}function n(v){v=p$(v);var z=Element.getStyle(v,"display");if(z&&z!=="none"){return{width:v.offsetWidth,height:v.offsetHeight}}var w=v.style;var u={visibility:w.visibility,position:w.position,display:w.display};var y={visibility:"hidden",display:"block"};if(u.position!=="fixed"){y.position="absolute"}Element.setStyle(v,y);var x={width:v.offsetWidth,height:v.offsetHeight};Element.setStyle(v,u);return x}function l(u){u=p$(u);if(e(u)||c(u)||m(u)||k(u)){return p$(document.body)}var v=(Element.getStyle(u,"display")==="inline");if(!v&&u.offsetParent){return p$(u.offsetParent)}while((u=u.parentNode)&&u!==document.body){if(Element.getStyle(u,"position")!=="static"){return k(u)?p$(document.body):p$(u)}}return p$(document.body)}function t(v){v=p$(v);var u=0,w=0;if(v.parentNode){do{u+=v.offsetTop||0;w+=v.offsetLeft||0;v=v.offsetParent}while(v)}return new Element.Offset(w,u)}function p(v){v=p$(v);var w=v.getLayout();var u=0,y=0;do{u+=v.offsetTop||0;y+=v.offsetLeft||0;v=v.offsetParent;if(v){if(m(v)){break}var x=Element.getStyle(v,"position");if(x!=="static"){break}}}while(v);y-=w.get("margin-top");u-=w.get("margin-left");return new Element.Offset(y,u)}function a(v){var u=0,w=0;do{u+=v.scrollTop||0;w+=v.scrollLeft||0;v=v.parentNode}while(v);return new Element.Offset(w,u)}function s(y){v=p$(v);var u=0,x=0,w=document.body;var v=y;do{u+=v.offsetTop||0;x+=v.offsetLeft||0;if(v.offsetParent==w&&Element.getStyle(v,"position")=="absolute"){break}}while(v=v.offsetParent);v=y;do{if(v!=w){u-=v.scrollTop||0;x-=v.scrollLeft||0}}while(v=v.parentNode);return new Element.Offset(x,u)}function q(u){u=p$(u);if(Element.getStyle(u,"position")==="absolute"){return u}var y=l(u);var x=u.viewportOffset(),v=y.viewportOffset();var z=x.relativeTo(v);var w=u.getLayout();u.store("prototype_absolutize_original_styles",{left:u.getStyle("left"),top:u.getStyle("top"),width:u.getStyle("width"),height:u.getStyle("height")});u.setStyle({position:"absolute",top:z.top+"px",left:z.left+"px",width:w.get("width")+"px",height:w.get("height")+"px"});return u}function i(v){v=p$(v);if(Element.getStyle(v,"position")==="relative"){return v}var u=v.retrieve("prototype_absolutize_original_styles");if(u){v.setStyle(u)}return v}if(Prototype.Browser.IE){l=l.wrap(function(w,v){v=p$(v);if(e(v)||c(v)||m(v)||k(v)){return p$(document.body)}var u=v.getStyle("position");if(u!=="static"){return w(v)}v.setStyle({position:"relative"});var x=w(v);v.setStyle({position:u});return x});p=p.wrap(function(x,v){v=p$(v);if(!v.parentNode){return new Element.Offset(0,0)}var u=v.getStyle("position");if(u!=="static"){return x(v)}var w=v.getOffsetParent();if(w&&w.getStyle("position")==="fixed"){d(w)}v.setStyle({position:"relative"});var y=x(v);v.setStyle({position:u});return y})}else{if(Prototype.Browser.Webkit){t=function(v){v=p$(v);var u=0,w=0;do{u+=v.offsetTop||0;w+=v.offsetLeft||0;if(v.offsetParent==document.body){if(Element.getStyle(v,"position")=="absolute"){break}}v=v.offsetParent}while(v);return new Element.Offset(w,u)}}}Element.addMethods({getLayout:r,measure:b,getDimensions:n,getOffsetParent:l,cumulativeOffset:t,positionedOffset:p,cumulativeScrollOffset:a,viewportOffset:s,absolutize:q,relativize:i});function m(u){return u.nodeName.toUpperCase()==="BODY"}function k(u){return u.nodeName.toUpperCase()==="HTML"}function e(u){return u.nodeType===Node.DOCUMENT_NODE}function c(u){return u!==document.body&&!Element.descendantOf(u,document.body)}if("getBoundingClientRect" in document.documentElement){Element.addMethods({viewportOffset:function(u){u=p$(u);if(c(u)){return new Element.Offset(0,0)}var v=u.getBoundingClientRect(),w=document.documentElement;return new Element.Offset(v.left-w.clientLeft,v.top-w.clientTop)}})}})();window.$$=function(){var a=$A(arguments).join(", ");return Prototype.Selector.select(a,document)};Prototype.Selector=(function(){function a(){throw new Error('Method "Prototype.Selector.select" must be defined.')}function c(){throw new Error('Method "Prototype.Selector.match" must be defined.')}function d(l,m,h){h=h||0;var g=Prototype.Selector.match,k=l.length,f=0,j;for(j=0;j<k;j++){if(g(l[j],m)&&h==f++){return Element.extend(l[j])}}}function e(h){for(var f=0,g=h.length;f<g;f++){Element.extend(h[f])}return h}var b=Prototype.K;return{select:a,match:c,find:d,extendElements:(Element.extend===b)?b:e,extendElement:Element.extend}})();Prototype._original_property=window.Sizzle;
/*
 * Sizzle CSS Selector Engine - v1.0
 *  Copyright 2009, The Dojo Foundation
 *  Released under the MIT, BSD, and GPL Licenses.
 *  More information: http://sizzlejs.com/
 */
(function(){var q=/((?:\((?:\([^()]+\)|[^()]+)+\)|\[(?:\[[^[\]]*\]|['"][^'"]*['"]|[^[\]'"]+)+\]|\\.|[^ >+~,(\[\\]+)+|[>+~])(\s*,\s*)?((?:.|\r|\n)*)/g,j=0,d=Object.prototype.toString,o=false,i=true;[0,0].sort(function(){i=false;return 0});var b=function(E,u,B,w){B=B||[];var e=u=u||document;if(u.nodeType!==1&&u.nodeType!==9){return[]}if(!E||typeof E!=="string"){return B}var C=[],D,z,I,H,A,t,s=true,x=p(u),G=E;while((q.exec(""),D=q.exec(G))!==null){G=D[3];C.push(D[1]);if(D[2]){t=D[3];break}}if(C.length>1&&k.exec(E)){if(C.length===2&&f.relative[C[0]]){z=g(C[0]+C[1],u)}else{z=f.relative[C[0]]?[u]:b(C.shift(),u);while(C.length){E=C.shift();if(f.relative[E]){E+=C.shift()}z=g(E,z)}}}else{if(!w&&C.length>1&&u.nodeType===9&&!x&&f.match.ID.test(C[0])&&!f.match.ID.test(C[C.length-1])){var J=b.find(C.shift(),u,x);u=J.expr?b.filter(J.expr,J.set)[0]:J.set[0]}if(u){var J=w?{expr:C.pop(),set:a(w)}:b.find(C.pop(),C.length===1&&(C[0]==="~"||C[0]==="+")&&u.parentNode?u.parentNode:u,x);z=J.expr?b.filter(J.expr,J.set):J.set;if(C.length>0){I=a(z)}else{s=false}while(C.length){var v=C.pop(),y=v;if(!f.relative[v]){v=""}else{y=C.pop()}if(y==null){y=u}f.relative[v](I,y,x)}}else{I=C=[]}}if(!I){I=z}if(!I){throw"Syntax error, unrecognized expression: "+(v||E)}if(d.call(I)==="[object Array]"){if(!s){B.push.apply(B,I)}else{if(u&&u.nodeType===1){for(var F=0;I[F]!=null;F++){if(I[F]&&(I[F]===true||I[F].nodeType===1&&h(u,I[F]))){B.push(z[F])}}}else{for(var F=0;I[F]!=null;F++){if(I[F]&&I[F].nodeType===1){B.push(z[F])}}}}}else{a(I,B)}if(t){b(t,e,B,w);b.uniqueSort(B)}return B};b.uniqueSort=function(s){if(c){o=i;s.sort(c);if(o){for(var e=1;e<s.length;e++){if(s[e]===s[e-1]){s.splice(e--,1)}}}}return s};b.matches=function(e,s){return b(e,null,null,s)};b.find=function(y,e,z){var x,v;if(!y){return[]}for(var u=0,t=f.order.length;u<t;u++){var w=f.order[u],v;if((v=f.leftMatch[w].exec(y))){var s=v[1];v.splice(1,1);if(s.substr(s.length-1)!=="\\"){v[1]=(v[1]||"").replace(/\\/g,"");x=f.find[w](v,e,z);if(x!=null){y=y.replace(f.match[w],"");break}}}}if(!x){x=e.getElementsByTagName("*")}return{set:x,expr:y}};b.filter=function(B,A,E,u){var t=B,G=[],y=A,w,e,x=A&&A[0]&&p(A[0]);while(B&&A.length){for(var z in f.filter){if((w=f.match[z].exec(B))!=null){var s=f.filter[z],F,D;e=false;if(y==G){G=[]}if(f.preFilter[z]){w=f.preFilter[z](w,y,E,G,u,x);if(!w){e=F=true}else{if(w===true){continue}}}if(w){for(var v=0;(D=y[v])!=null;v++){if(D){F=s(D,w,v,y);var C=u^!!F;if(E&&F!=null){if(C){e=true}else{y[v]=false}}else{if(C){G.push(D);e=true}}}}}if(F!==undefined){if(!E){y=G}B=B.replace(f.match[z],"");if(!e){return[]}break}}}if(B==t){if(e==null){throw"Syntax error, unrecognized expression: "+B}else{break}}t=B}return y};var f=b.selectors={order:["ID","NAME","TAG"],match:{ID:/#((?:[\w\u00c0-\uFFFF-]|\\.)+)/,CLASS:/\.((?:[\w\u00c0-\uFFFF-]|\\.)+)/,NAME:/\[name=['"]*((?:[\w\u00c0-\uFFFF-]|\\.)+)['"]*\]/,ATTR:/\[\s*((?:[\w\u00c0-\uFFFF-]|\\.)+)\s*(?:(\S?=)\s*(['"]*)(.*?)\3|)\s*\]/,TAG:/^((?:[\w\u00c0-\uFFFF\*-]|\\.)+)/,CHILD:/:(only|nth|last|first)-child(?:\((even|odd|[\dn+-]*)\))?/,POS:/:(nth|eq|gt|lt|first|last|even|odd)(?:\((\d*)\))?(?=[^-]|$)/,PSEUDO:/:((?:[\w\u00c0-\uFFFF-]|\\.)+)(?:\((['"]*)((?:\([^\)]+\)|[^\2\(\)]*)+)\2\))?/},leftMatch:{},attrMap:{"class":"className","for":"htmlFor"},attrHandle:{href:function(e){return e.getAttribute("href")}},relative:{"+":function(y,e,x){var v=typeof e==="string",z=v&&!/\W/.test(e),w=v&&!z;if(z&&!x){e=e.toUpperCase()}for(var u=0,t=y.length,s;u<t;u++){if((s=y[u])){while((s=s.previousSibling)&&s.nodeType!==1){}y[u]=w||s&&s.nodeName===e?s||false:s===e}}if(w){b.filter(e,y,true)}},">":function(x,s,y){var v=typeof s==="string";if(v&&!/\W/.test(s)){s=y?s:s.toUpperCase();for(var t=0,e=x.length;t<e;t++){var w=x[t];if(w){var u=w.parentNode;x[t]=u.nodeName===s?u:false}}}else{for(var t=0,e=x.length;t<e;t++){var w=x[t];if(w){x[t]=v?w.parentNode:w.parentNode===s}}if(v){b.filter(s,x,true)}}},"":function(u,s,w){var t=j++,e=r;if(!/\W/.test(s)){var v=s=w?s:s.toUpperCase();e=n}e("parentNode",s,t,u,v,w)},"~":function(u,s,w){var t=j++,e=r;if(typeof s==="string"&&!/\W/.test(s)){var v=s=w?s:s.toUpperCase();e=n}e("previousSibling",s,t,u,v,w)}},find:{ID:function(s,t,u){if(typeof t.getElementById!=="undefined"&&!u){var e=t.getElementById(s[1]);return e?[e]:[]}},NAME:function(t,w,x){if(typeof w.getElementsByName!=="undefined"){var s=[],v=w.getElementsByName(t[1]);for(var u=0,e=v.length;u<e;u++){if(v[u].getAttribute("name")===t[1]){s.push(v[u])}}return s.length===0?null:s}},TAG:function(e,s){return s.getElementsByTagName(e[1])}},preFilter:{CLASS:function(u,s,t,e,x,y){u=" "+u[1].replace(/\\/g,"")+" ";if(y){return u}for(var v=0,w;(w=s[v])!=null;v++){if(w){if(x^(w.className&&(" "+w.className+" ").indexOf(u)>=0)){if(!t){e.push(w)}}else{if(t){s[v]=false}}}}return false},ID:function(e){return e[1].replace(/\\/g,"")},TAG:function(s,e){for(var t=0;e[t]===false;t++){}return e[t]&&p(e[t])?s[1]:s[1].toUpperCase()},CHILD:function(e){if(e[1]=="nth"){var s=/(-?)(\d*)n((?:\+|-)?\d*)/.exec(e[2]=="even"&&"2n"||e[2]=="odd"&&"2n+1"||!/\D/.test(e[2])&&"0n+"+e[2]||e[2]);e[2]=(s[1]+(s[2]||1))-0;e[3]=s[3]-0}e[0]=j++;return e},ATTR:function(v,s,t,e,w,x){var u=v[1].replace(/\\/g,"");if(!x&&f.attrMap[u]){v[1]=f.attrMap[u]}if(v[2]==="~="){v[4]=" "+v[4]+" "}return v},PSEUDO:function(v,s,t,e,w){if(v[1]==="not"){if((q.exec(v[3])||"").length>1||/^\w/.test(v[3])){v[3]=b(v[3],null,null,s)}else{var u=b.filter(v[3],s,t,true^w);if(!t){e.push.apply(e,u)}return false}}else{if(f.match.POS.test(v[0])||f.match.CHILD.test(v[0])){return true}}return v},POS:function(e){e.unshift(true);return e}},filters:{enabled:function(e){return e.disabled===false&&e.type!=="hidden"},disabled:function(e){return e.disabled===true},checked:function(e){return e.checked===true},selected:function(e){e.parentNode.selectedIndex;return e.selected===true},parent:function(e){return !!e.firstChild},empty:function(e){return !e.firstChild},has:function(t,s,e){return !!b(e[3],t).length},header:function(e){return/h\d/i.test(e.nodeName)},text:function(e){return"text"===e.type},radio:function(e){return"radio"===e.type},checkbox:function(e){return"checkbox"===e.type},file:function(e){return"file"===e.type},password:function(e){return"password"===e.type},submit:function(e){return"submit"===e.type},image:function(e){return"image"===e.type},reset:function(e){return"reset"===e.type},button:function(e){return"button"===e.type||e.nodeName.toUpperCase()==="BUTTON"},input:function(e){return/input|select|textarea|button/i.test(e.nodeName)}},setFilters:{first:function(s,e){return e===0},last:function(t,s,e,u){return s===u.length-1},even:function(s,e){return e%2===0},odd:function(s,e){return e%2===1},lt:function(t,s,e){return s<e[3]-0},gt:function(t,s,e){return s>e[3]-0},nth:function(t,s,e){return e[3]-0==s},eq:function(t,s,e){return e[3]-0==s}},filter:{PSEUDO:function(x,t,u,y){var s=t[1],v=f.filters[s];if(v){return v(x,u,t,y)}else{if(s==="contains"){return(x.textContent||x.innerText||"").indexOf(t[3])>=0}else{if(s==="not"){var w=t[3];for(var u=0,e=w.length;u<e;u++){if(w[u]===x){return false}}return true}}}},CHILD:function(e,u){var x=u[1],s=e;switch(x){case"only":case"first":while((s=s.previousSibling)){if(s.nodeType===1){return false}}if(x=="first"){return true}s=e;case"last":while((s=s.nextSibling)){if(s.nodeType===1){return false}}return true;case"nth":var t=u[2],A=u[3];if(t==1&&A==0){return true}var w=u[0],z=e.parentNode;if(z&&(z.sizcache!==w||!e.nodeIndex)){var v=0;for(s=z.firstChild;s;s=s.nextSibling){if(s.nodeType===1){s.nodeIndex=++v}}z.sizcache=w}var y=e.nodeIndex-A;if(t==0){return y==0}else{return(y%t==0&&y/t>=0)}}},ID:function(s,e){return s.nodeType===1&&s.getAttribute("id")===e},TAG:function(s,e){return(e==="*"&&s.nodeType===1)||s.nodeName===e},CLASS:function(s,e){return(" "+(s.className||s.getAttribute("class"))+" ").indexOf(e)>-1},ATTR:function(w,u){var t=u[1],e=f.attrHandle[t]?f.attrHandle[t](w):w[t]!=null?w[t]:w.getAttribute(t),x=e+"",v=u[2],s=u[4];return e==null?v==="!=":v==="="?x===s:v==="*="?x.indexOf(s)>=0:v==="~="?(" "+x+" ").indexOf(s)>=0:!s?x&&e!==false:v==="!="?x!=s:v==="^="?x.indexOf(s)===0:v==="$="?x.substr(x.length-s.length)===s:v==="|="?x===s||x.substr(0,s.length+1)===s+"-":false},POS:function(v,s,t,w){var e=s[2],u=f.setFilters[e];if(u){return u(v,t,s,w)}}}};var k=f.match.POS;for(var m in f.match){f.match[m]=new RegExp(f.match[m].source+/(?![^\[]*\])(?![^\(]*\))/.source);f.leftMatch[m]=new RegExp(/(^(?:.|\r|\n)*?)/.source+f.match[m].source)}var a=function(s,e){s=Array.prototype.slice.call(s,0);if(e){e.push.apply(e,s);return e}return s};try{Array.prototype.slice.call(document.documentElement.childNodes,0)}catch(l){a=function(v,u){var s=u||[];if(d.call(v)==="[object Array]"){Array.prototype.push.apply(s,v)}else{if(typeof v.length==="number"){for(var t=0,e=v.length;t<e;t++){s.push(v[t])}}else{for(var t=0;v[t];t++){s.push(v[t])}}}return s}}var c;if(document.documentElement.compareDocumentPosition){c=function(s,e){if(!s.compareDocumentPosition||!e.compareDocumentPosition){if(s==e){o=true}return 0}var t=s.compareDocumentPosition(e)&4?-1:s===e?0:1;if(t===0){o=true}return t}}else{if("sourceIndex" in document.documentElement){c=function(s,e){if(!s.sourceIndex||!e.sourceIndex){if(s==e){o=true}return 0}var t=s.sourceIndex-e.sourceIndex;if(t===0){o=true}return t}}else{if(document.createRange){c=function(u,s){if(!u.ownerDocument||!s.ownerDocument){if(u==s){o=true}return 0}var t=u.ownerDocument.createRange(),e=s.ownerDocument.createRange();t.setStart(u,0);t.setEnd(u,0);e.setStart(s,0);e.setEnd(s,0);var v=t.compareBoundaryPoints(Range.START_TO_END,e);if(v===0){o=true}return v}}}}(function(){var s=document.createElement("div"),t="script"+(new Date).getTime();s.innerHTML="<a name='"+t+"'/>";var e=document.documentElement;e.insertBefore(s,e.firstChild);if(!!document.getElementById(t)){f.find.ID=function(v,w,x){if(typeof w.getElementById!=="undefined"&&!x){var u=w.getElementById(v[1]);return u?u.id===v[1]||typeof u.getAttributeNode!=="undefined"&&u.getAttributeNode("id").nodeValue===v[1]?[u]:undefined:[]}};f.filter.ID=function(w,u){var v=typeof w.getAttributeNode!=="undefined"&&w.getAttributeNode("id");return w.nodeType===1&&v&&v.nodeValue===u}}e.removeChild(s);e=s=null})();(function(){var e=document.createElement("div");e.appendChild(document.createComment(""));if(e.getElementsByTagName("*").length>0){f.find.TAG=function(s,w){var v=w.getElementsByTagName(s[1]);if(s[1]==="*"){var u=[];for(var t=0;v[t];t++){if(v[t].nodeType===1){u.push(v[t])}}v=u}return v}}e.innerHTML="<a href='#'></a>";if(e.firstChild&&typeof e.firstChild.getAttribute!=="undefined"&&e.firstChild.getAttribute("href")!=="#"){f.attrHandle.href=function(s){return s.getAttribute("href",2)}}e=null})();if(document.querySelectorAll){(function(){var e=b,t=document.createElement("div");t.innerHTML="<p class='TEST'></p>";if(t.querySelectorAll&&t.querySelectorAll(".TEST").length===0){return}b=function(x,w,u,v){w=w||document;if(!v&&w.nodeType===9&&!p(w)){try{return a(w.querySelectorAll(x),u)}catch(y){}}return e(x,w,u,v)};for(var s in e){b[s]=e[s]}t=null})()}if(document.getElementsByClassName&&document.documentElement.getElementsByClassName){(function(){var e=document.createElement("div");e.innerHTML="<div class='test e'></div><div class='test'></div>";if(e.getElementsByClassName("e").length===0){return}e.lastChild.className="e";if(e.getElementsByClassName("e").length===1){return}f.order.splice(1,0,"CLASS");f.find.CLASS=function(s,t,u){if(typeof t.getElementsByClassName!=="undefined"&&!u){return t.getElementsByClassName(s[1])}};e=null})()}function n(s,x,w,B,y,A){var z=s=="previousSibling"&&!A;for(var u=0,t=B.length;u<t;u++){var e=B[u];if(e){if(z&&e.nodeType===1){e.sizcache=w;e.sizset=u}e=e[s];var v=false;while(e){if(e.sizcache===w){v=B[e.sizset];break}if(e.nodeType===1&&!A){e.sizcache=w;e.sizset=u}if(e.nodeName===x){v=e;break}e=e[s]}B[u]=v}}}function r(s,x,w,B,y,A){var z=s=="previousSibling"&&!A;for(var u=0,t=B.length;u<t;u++){var e=B[u];if(e){if(z&&e.nodeType===1){e.sizcache=w;e.sizset=u}e=e[s];var v=false;while(e){if(e.sizcache===w){v=B[e.sizset];break}if(e.nodeType===1){if(!A){e.sizcache=w;e.sizset=u}if(typeof x!=="string"){if(e===x){v=true;break}}else{if(b.filter(x,[e]).length>0){v=e;break}}}e=e[s]}B[u]=v}}}var h=document.compareDocumentPosition?function(s,e){return s.compareDocumentPosition(e)&16}:function(s,e){return s!==e&&(s.contains?s.contains(e):true)};var p=function(e){return e.nodeType===9&&e.documentElement.nodeName!=="HTML"||!!e.ownerDocument&&e.ownerDocument.documentElement.nodeName!=="HTML"};var g=function(e,y){var u=[],v="",w,t=y.nodeType?[y]:y;while((w=f.match.PSEUDO.exec(e))){v+=w[0];e=e.replace(f.match.PSEUDO,"")}e=f.relative[e]?e+"*":e;for(var x=0,s=t.length;x<s;x++){b(e,t[x],u)}return b.filter(v,u)};window.Sizzle=b})();(function(c){var d=Prototype.Selector.extendElements;function a(e,f){return d(c(e,f||document))}function b(f,e){return c.matches(e,[f]).length==1}Prototype.Selector.engine=c;Prototype.Selector.select=a;Prototype.Selector.match=b})(Sizzle);window.Sizzle=Prototype._original_property;delete Prototype._original_property;var Form={reset:function(a){a=p$(a);a.reset();return a},serializeElements:function(h,d){if(typeof d!="object"){d={hash:!!d}}else{if(Object.isUndefined(d.hash)){d.hash=true}}var e,g,a=false,f=d.submit,b,c;if(d.hash){c={};b=function(i,j,k){if(j in i){if(!Object.isArray(i[j])){i[j]=[i[j]]}i[j].push(k)}else{i[j]=k}return i}}else{c="";b=function(i,j,k){return i+(i?"&":"")+encodeURIComponent(j)+"="+encodeURIComponent(k)}}return h.inject(c,function(i,j){if(!j.disabled&&j.name){e=j.name;g=p$(j).getValue();if(g!=null&&j.type!="file"&&(j.type!="submit"||(!a&&f!==false&&(!f||e==f)&&(a=true)))){i=b(i,e,g)}}return i})}};Form.Methods={serialize:function(b,a){return Form.serializeElements(Form.getElements(b),a)},getElements:function(e){var f=p$(e).getElementsByTagName("*"),d,a=[],c=Form.Element.Serializers;for(var b=0;d=f[b];b++){a.push(d)}return a.inject([],function(g,h){if(c[h.tagName.toLowerCase()]){g.push(Element.extend(h))}return g})},getInputs:function(g,c,d){g=p$(g);var a=g.getElementsByTagName("input");if(!c&&!d){return $A(a).map(Element.extend)}for(var e=0,h=[],f=a.length;e<f;e++){var b=a[e];if((c&&b.type!=c)||(d&&b.name!=d)){continue}h.push(Element.extend(b))}return h},disable:function(a){a=p$(a);Form.getElements(a).invoke("disable");return a},enable:function(a){a=p$(a);Form.getElements(a).invoke("enable");return a},findFirstElement:function(b){var c=p$(b).getElements().findAll(function(d){return"hidden"!=d.type&&!d.disabled});var a=c.findAll(function(d){return d.hasAttribute("tabIndex")&&d.tabIndex>=0}).sortBy(function(d){return d.tabIndex}).first();return a?a:c.find(function(d){return/^(?:input|select|textarea)$/i.test(d.tagName)})},focusFirstElement:function(b){b=p$(b);var a=b.findFirstElement();if(a){a.activate()}return b},request:function(b,a){b=p$(b),a=Object.clone(a||{});var d=a.parameters,c=b.readAttribute("action")||"";if(c.blank()){c=window.location.href}a.parameters=b.serialize(true);if(d){if(Object.isString(d)){d=d.toQueryParams()}Object.extend(a.parameters,d)}if(b.hasAttribute("method")&&!a.method){a.method=b.method}return new Ajax.Request(c,a)}};Form.Element={focus:function(a){p$(a).focus();return a},select:function(a){p$(a).select();return a}};Form.Element.Methods={serialize:function(a){a=p$(a);if(!a.disabled&&a.name){var b=a.getValue();if(b!=undefined){var c={};c[a.name]=b;return Object.toQueryString(c)}}return""},getValue:function(a){a=p$(a);var b=a.tagName.toLowerCase();return Form.Element.Serializers[b](a)},setValue:function(a,b){a=p$(a);var c=a.tagName.toLowerCase();Form.Element.Serializers[c](a,b);return a},clear:function(a){p$(a).value="";return a},present:function(a){return p$(a).value!=""},activate:function(a){a=p$(a);try{a.focus();if(a.select&&(a.tagName.toLowerCase()!="input"||!(/^(?:button|reset|submit)$/i.test(a.type)))){a.select()}}catch(b){}return a},disable:function(a){a=p$(a);a.disabled=true;return a},enable:function(a){a=p$(a);a.disabled=false;return a}};var Field=Form.Element;var $F=Form.Element.Methods.getValue;Form.Element.Serializers=(function(){function b(h,i){switch(h.type.toLowerCase()){case"checkbox":case"radio":return f(h,i);default:return e(h,i)}}function f(h,i){if(Object.isUndefined(i)){return h.checked?h.value:null}else{h.checked=!!i}}function e(h,i){if(Object.isUndefined(i)){return h.value}else{h.value=i}}function a(k,n){if(Object.isUndefined(n)){return(k.type==="select-one"?c:d)(k)}var j,l,o=!Object.isArray(n);for(var h=0,m=k.length;h<m;h++){j=k.options[h];l=this.optionValue(j);if(o){if(l==n){j.selected=true;return}}else{j.selected=n.include(l)}}}function c(i){var h=i.selectedIndex;return h>=0?g(i.options[h]):null}function d(l){var h,m=l.length;if(!m){return null}for(var k=0,h=[];k<m;k++){var j=l.options[k];if(j.selected){h.push(g(j))}}return h}function g(h){return Element.hasAttribute(h,"value")?h.value:h.text}return{input:b,inputSelector:f,textarea:e,select:a,selectOne:c,selectMany:d,optionValue:g,button:e}})();Abstract.TimedObserver=Class.create(PeriodicalExecuter,{initialize:function($super,a,b,c){$super(c,b);this.element=p$(a);this.lastValue=this.getValue()},execute:function(){var a=this.getValue();if(Object.isString(this.lastValue)&&Object.isString(a)?this.lastValue!=a:String(this.lastValue)!=String(a)){this.callback(this.element,a);this.lastValue=a}}});Form.Element.Observer=Class.create(Abstract.TimedObserver,{getValue:function(){return Form.Element.getValue(this.element)}});Form.Observer=Class.create(Abstract.TimedObserver,{getValue:function(){return Form.serialize(this.element)}});Abstract.EventObserver=Class.create({initialize:function(a,b){this.element=p$(a);this.callback=b;this.lastValue=this.getValue();if(this.element.tagName.toLowerCase()=="form"){this.registerFormCallbacks()}else{this.registerCallback(this.element)}},onElementEvent:function(){var a=this.getValue();if(this.lastValue!=a){this.callback(this.element,a);this.lastValue=a}},registerFormCallbacks:function(){Form.getElements(this.element).each(this.registerCallback,this)},registerCallback:function(a){if(a.type){switch(a.type.toLowerCase()){case"checkbox":case"radio":Event.observe(a,"click",this.onElementEvent.bind(this));break;default:Event.observe(a,"change",this.onElementEvent.bind(this));break}}}});Form.Element.EventObserver=Class.create(Abstract.EventObserver,{getValue:function(){return Form.Element.getValue(this.element)}});Form.EventObserver=Class.create(Abstract.EventObserver,{getValue:function(){return Form.serialize(this.element)}});(function(){var C={KEY_BACKSPACE:8,KEY_TAB:9,KEY_RETURN:13,KEY_ESC:27,KEY_LEFT:37,KEY_UP:38,KEY_RIGHT:39,KEY_DOWN:40,KEY_DELETE:46,KEY_HOME:36,KEY_END:35,KEY_PAGEUP:33,KEY_PAGEDOWN:34,KEY_INSERT:45,cache:{}};var f=document.documentElement;var D="onmouseenter" in f&&"onmouseleave" in f;var a=function(E){return false};if(window.attachEvent){if(window.addEventListener){a=function(E){return !(E instanceof window.Event)}}else{a=function(E){return true}}}var r;function A(F,E){return F.which?(F.which===E+1):(F.button===E)}var o={0:1,1:4,2:2};function y(F,E){return F.button===o[E]}function B(F,E){switch(E){case 0:return F.which==1&&!F.metaKey;case 1:return F.which==2||(F.which==1&&F.metaKey);case 2:return F.which==3;default:return false}}if(window.attachEvent){if(!window.addEventListener){r=y}else{r=function(F,E){return a(F)?y(F,E):A(F,E)}}}else{if(Prototype.Browser.WebKit){r=B}else{r=A}}function v(E){return r(E,0)}function t(E){return r(E,1)}function n(E){return r(E,2)}function d(G){G=C.extend(G);var F=G.target,E=G.type,H=G.currentTarget;if(H&&H.tagName){if(E==="load"||E==="error"||(E==="click"&&H.tagName.toLowerCase()==="input"&&H.type==="radio")){F=H}}if(F.nodeType==Node.TEXT_NODE){F=F.parentNode}return Element.extend(F)}function p(F,G){var E=C.element(F);if(!G){return E}while(E){if(Object.isElement(E)&&Prototype.Selector.match(E,G)){return Element.extend(E)}E=E.parentNode}}function s(E){return{x:c(E),y:b(E)}}function c(G){var F=document.documentElement,E=document.body||{scrollLeft:0};return G.pageX||(G.clientX+(F.scrollLeft||E.scrollLeft)-(F.clientLeft||0))}function b(G){var F=document.documentElement,E=document.body||{scrollTop:0};return G.pageY||(G.clientY+(F.scrollTop||E.scrollTop)-(F.clientTop||0))}function q(E){C.extend(E);E.preventDefault();E.stopPropagation();E.stopped=true}C.Methods={isLeftClick:v,isMiddleClick:t,isRightClick:n,element:d,findElement:p,pointer:s,pointerX:c,pointerY:b,stop:q};var x=Object.keys(C.Methods).inject({},function(E,F){E[F]=C.Methods[F].methodize();return E});if(window.attachEvent){function i(F){var E;switch(F.type){case"mouseover":case"mouseenter":E=F.fromElement;break;case"mouseout":case"mouseleave":E=F.toElement;break;default:return null}return Element.extend(E)}var u={stopPropagation:function(){this.cancelBubble=true},preventDefault:function(){this.returnValue=false},inspect:function(){return"[object Event]"}};C.extend=function(F,E){if(!F){return false}if(!a(F)){return F}if(F._extendedByPrototype){return F}F._extendedByPrototype=Prototype.emptyFunction;var G=C.pointer(F);Object.extend(F,{target:F.srcElement||E,relatedTarget:i(F),pageX:G.x,pageY:G.y});Object.extend(F,x);Object.extend(F,u);return F}}else{C.extend=Prototype.K}if(window.addEventListener){C.prototype=window.Event.prototype||document.createEvent("HTMLEvents").__proto__;Object.extend(C.prototype,x)}function m(I,H,J){var G=Element.retrieve(I,"prototype_event_registry");if(Object.isUndefined(G)){e.push(I);G=Element.retrieve(I,"prototype_event_registry",$H())}var E=G.get(H);if(Object.isUndefined(E)){E=[];G.set(H,E)}if(E.pluck("handler").include(J)){return false}var F;if(H.include(":")){F=function(K){if(Object.isUndefined(K.eventName)){return false}if(K.eventName!==H){return false}C.extend(K,I);J.call(I,K)}}else{if(!D&&(H==="mouseenter"||H==="mouseleave")){if(H==="mouseenter"||H==="mouseleave"){F=function(L){C.extend(L,I);var K=L.relatedTarget;while(K&&K!==I){try{K=K.parentNode}catch(M){K=I}}if(K===I){return}J.call(I,L)}}}else{F=function(K){C.extend(K,I);J.call(I,K)}}}F.handler=J;E.push(F);return F}function h(){for(var E=0,F=e.length;E<F;E++){C.stopObserving(e[E]);e[E]=null}}var e=[];if(Prototype.Browser.IE){window.attachEvent("onunload",h)}if(Prototype.Browser.WebKit){window.addEventListener("unload",Prototype.emptyFunction,false)}var l=Prototype.K,g={mouseenter:"mouseover",mouseleave:"mouseout"};if(!D){l=function(E){return(g[E]||E)}}function w(H,G,I){H=p$(H);var F=m(H,G,I);if(!F){return H}if(G.include(":")){if(H.addEventListener){H.addEventListener("dataavailable",F,false)}else{H.attachEvent("ondataavailable",F);H.attachEvent("onlosecapture",F)}}else{var E=l(G);if(H.addEventListener){H.addEventListener(E,F,false)}else{H.attachEvent("on"+E,F)}}return H}function k(K,H,L){K=p$(K);var G=Element.retrieve(K,"prototype_event_registry");if(!G){return K}if(!H){G.each(function(N){var M=N.key;k(K,M)});return K}var I=G.get(H);if(!I){return K}if(!L){I.each(function(M){k(K,H,M.handler)});return K}var J=I.length,F;while(J--){if(I[J].handler===L){F=I[J];break}}if(!F){return K}if(H.include(":")){if(K.removeEventListener){K.removeEventListener("dataavailable",F,false)}else{K.detachEvent("ondataavailable",F);K.detachEvent("onlosecapture",F)}}else{var E=l(H);if(K.removeEventListener){K.removeEventListener(E,F,false)}else{K.detachEvent("on"+E,F)}}G.set(H,I.without(F));return K}function z(H,G,F,E){H=p$(H);if(Object.isUndefined(E)){E=true}if(H==document&&document.createEvent&&!H.dispatchEvent){H=document.documentElement}var I;if(document.createEvent){I=document.createEvent("HTMLEvents");I.initEvent("dataavailable",E,true)}else{I=document.createEventObject();I.eventType=E?"ondataavailable":"onlosecapture"}I.eventName=G;I.memo=F||{};if(document.createEvent){H.dispatchEvent(I)}else{H.fireEvent(I.eventType,I)}return C.extend(I)}C.Handler=Class.create({initialize:function(G,F,E,H){this.element=p$(G);this.eventName=F;this.selector=E;this.callback=H;this.handler=this.handleEvent.bind(this)},start:function(){C.observe(this.element,this.eventName,this.handler);return this},stop:function(){C.stopObserving(this.element,this.eventName,this.handler);return this},handleEvent:function(F){var E=C.findElement(F,this.selector);if(E){this.callback.call(this.element,F,E)}}});function j(G,F,E,H){G=p$(G);if(Object.isFunction(E)&&Object.isUndefined(H)){H=E,E=null}return new C.Handler(G,F,E,H).start()}Object.extend(C,C.Methods);Object.extend(C,{fire:z,observe:w,stopObserving:k,on:j});Element.addMethods({fire:z,observe:w,stopObserving:k,on:j});Object.extend(document,{fire:z.methodize(),observe:w.methodize(),stopObserving:k.methodize(),on:j.methodize(),loaded:false});if(window.Event){Object.extend(window.Event,C)}else{window.Event=C}})();(function(){var d;function a(){if(document.loaded){return}if(d){window.clearTimeout(d)}document.loaded=true;document.fire("dom:loaded")}function c(){if(document.readyState==="complete"){document.stopObserving("readystatechange",c);a()}}function b(){try{document.documentElement.doScroll("left")}catch(f){d=b.defer();return}a()}if(document.addEventListener){document.addEventListener("DOMContentLoaded",a,false)}else{document.observe("readystatechange",c);if(window==top){d=b.defer()}}Event.observe(window,"load",a)})();Element.addMethods();Hash.toQueryString=Object.toQueryString;var Toggle={display:Element.toggle};Element.Methods.childOf=Element.Methods.descendantOf;var Insertion={Before:function(a,b){return Element.insert(a,{before:b})},Top:function(a,b){return Element.insert(a,{top:b})},Bottom:function(a,b){return Element.insert(a,{bottom:b})},After:function(a,b){return Element.insert(a,{after:b})}};var $continue=new Error('"throw $continue" is deprecated, use "return" instead');var Position={includeScrollOffsets:false,prepare:function(){this.deltaX=window.pageXOffset||document.documentElement.scrollLeft||document.body.scrollLeft||0;this.deltaY=window.pageYOffset||document.documentElement.scrollTop||document.body.scrollTop||0},within:function(b,a,c){if(this.includeScrollOffsets){return this.withinIncludingScrolloffsets(b,a,c)}this.xcomp=a;this.ycomp=c;this.offset=Element.cumulativeOffset(b);return(c>=this.offset[1]&&c<this.offset[1]+b.offsetHeight&&a>=this.offset[0]&&a<this.offset[0]+b.offsetWidth)},withinIncludingScrolloffsets:function(b,a,d){var c=Element.cumulativeScrollOffset(b);this.xcomp=a+c[0]-this.deltaX;this.ycomp=d+c[1]-this.deltaY;this.offset=Element.cumulativeOffset(b);return(this.ycomp>=this.offset[1]&&this.ycomp<this.offset[1]+b.offsetHeight&&this.xcomp>=this.offset[0]&&this.xcomp<this.offset[0]+b.offsetWidth)},overlap:function(b,a){if(!b){return 0}if(b=="vertical"){return((this.offset[1]+a.offsetHeight)-this.ycomp)/a.offsetHeight}if(b=="horizontal"){return((this.offset[0]+a.offsetWidth)-this.xcomp)/a.offsetWidth}},cumulativeOffset:Element.Methods.cumulativeOffset,positionedOffset:Element.Methods.positionedOffset,absolutize:function(a){Position.prepare();return Element.absolutize(a)},relativize:function(a){Position.prepare();return Element.relativize(a)},realOffset:Element.Methods.cumulativeScrollOffset,offsetParent:Element.Methods.getOffsetParent,page:Element.Methods.viewportOffset,clone:function(b,c,a){a=a||{};return Element.clonePosition(c,b,a)}};if(!document.getElementsByClassName){document.getElementsByClassName=function(b){function a(c){return c.blank()?null:"[contains(concat(' ', @class, ' '), ' "+c+" ')]"}b.getElementsByClassName=Prototype.BrowserFeatures.XPath?function(c,e){e=e.toString().strip();var d=/\s/.test(e)?$w(e).map(a).join(""):a(e);return d?document._getElementsByXPath(".//*"+d,c):[]}:function(e,f){f=f.toString().strip();var g=[],h=(/\s/.test(f)?$w(f):null);if(!h&&!f){return g}var c=p$(e).getElementsByTagName("*");f=" "+f+" ";for(var d=0,k,j;k=c[d];d++){if(k.className&&(j=" "+k.className+" ")&&(j.include(f)||(h&&h.all(function(i){return !i.toString().blank()&&j.include(" "+i+" ")})))){g.push(Element.extend(k))}}return g};return function(d,c){return p$(c||document.body).getElementsByClassName(d)}}(Element.Methods)}Element.ClassNames=Class.create();Element.ClassNames.prototype={initialize:function(a){this.element=p$(a)},_each:function(a){this.element.className.split(/\s+/).select(function(b){return b.length>0})._each(a)},set:function(a){this.element.className=a},add:function(a){if(this.include(a)){return}this.set($A(this).concat(a).join(" "))},remove:function(a){if(!this.include(a)){return}this.set($A(this).without(a).join(" "))},toString:function(){return $A(this).join(" ")}};Object.extend(Element.ClassNames.prototype,Enumerable);(function(){window.Selector=Class.create({initialize:function(a){this.expression=a.strip()},findElements:function(a){return Prototype.Selector.select(this.expression,a)},match:function(a){return Prototype.Selector.match(a,this.expression)},toString:function(){return this.expression},inspect:function(){return"#<Selector: "+this.expression+">"}});Object.extend(Selector,{matchElements:function(f,g){var a=Prototype.Selector.match,d=[];for(var c=0,e=f.length;c<e;c++){var b=f[c];if(a(b,g)){d.push(Element.extend(b))}}return d},findElement:function(f,g,b){b=b||0;var a=0,d;for(var c=0,e=f.length;c<e;c++){d=f[c];if(Prototype.Selector.match(d,g)&&b===a++){return Element.extend(d)}}},findChildElements:function(b,c){var a=c.toArray().join(", ");return Prototype.Selector.select(a,b||document)}})})();
/*!
 * Raphael 1.5.2 - JavaScript Vector Library
 *
 * Copyright (c) 2010 Dmitry Baranovskiy (http://raphaeljs.com)
 * Licensed under the MIT (http://raphaeljs.com/license.html) license.
 */
(function () {
    function R() {
        if (R.is(arguments[0], array)) {
            var a = arguments[0],
                cnv = create[apply](R, a.splice(0, 3 + R.is(a[0], nu))),
                res = cnv.set();
            for (var i = 0, ii = a[length]; i < ii; i++) {
                var j = a[i] || {};
                elements[has](j.type) && res[push](cnv[j.type]().attr(j));
            }
            return res;
        }
        return create[apply](R, arguments);
    }
    R.version = "1.5.2";
    var separator = /[, ]+/,
        elements = {circle: 1, rect: 1, path: 1, ellipse: 1, text: 1, image: 1},
        formatrg = /\{(\d+)\}/g,
        proto = "prototype",
        has = "hasOwnProperty",
        doc = document,
        win = window,
        oldRaphael = {
            was: Object[proto][has].call(win, "Raphael"),
            is: win.Raphael
        },
        Paper = function () {
            this.customAttributes = {};
        },
        paperproto,
        appendChild = "appendChild",
        apply = "apply",
        concat = "concat",
        supportsTouch = "createTouch" in doc,
        E = "",
        S = " ",
        Str = String,
        split = "split",
        events = "click dblclick mousedown mousemove mouseout mouseover mouseup touchstart touchmove touchend orientationchange touchcancel gesturestart gesturechange gestureend"[split](S),
        touchMap = {
            mousedown: "touchstart",
            mousemove: "touchmove",
            mouseup: "touchend"
        },
        join = "join",
        length = "length",
        lowerCase = Str[proto].toLowerCase,
        math = Math,
        mmax = math.max,
        mmin = math.min,
        abs = math.abs,
        pow = math.pow,
        PI = math.PI,
        nu = "number",
        string = "string",
        array = "array",
        toString = "toString",
        fillString = "fill",
        objectToString = Object[proto][toString],
        paper = {},
        push = "push",
        ISURL = /^url\(['"]?([^\)]+?)['"]?\)$/i,
        colourRegExp = /^\s*((#[a-f\d]{6})|(#[a-f\d]{3})|rgba?\(\s*([\d\.]+%?\s*,\s*[\d\.]+%?\s*,\s*[\d\.]+(?:%?\s*,\s*[\d\.]+)?)%?\s*\)|hsba?\(\s*([\d\.]+(?:deg|\xb0|%)?\s*,\s*[\d\.]+%?\s*,\s*[\d\.]+(?:%?\s*,\s*[\d\.]+)?)%?\s*\)|hsla?\(\s*([\d\.]+(?:deg|\xb0|%)?\s*,\s*[\d\.]+%?\s*,\s*[\d\.]+(?:%?\s*,\s*[\d\.]+)?)%?\s*\))\s*$/i,
        isnan = {"NaN": 1, "Infinity": 1, "-Infinity": 1},
        bezierrg = /^(?:cubic-)?bezier\(([^,]+),([^,]+),([^,]+),([^\)]+)\)/,
        round = math.round,
        setAttribute = "setAttribute",
        toFloat = parseFloat,
        toInt = parseInt,
        ms = " progid:DXImageTransform.Microsoft",
        upperCase = Str[proto].toUpperCase,
        availableAttrs = {blur: 0, "clip-rect": "0 0 1e9 1e9", cursor: "default", cx: 0, cy: 0, fill: "#fff", "fill-opacity": 1, font: '10px "Arial"', "font-family": '"Arial"', "font-size": "10", "font-style": "normal", "font-weight": 400, gradient: 0, height: 0, href: "http://raphaeljs.com/", opacity: 1, path: "M0,0", r: 0, rotation: 0, rx: 0, ry: 0, scale: "1 1", src: "", stroke: "#000", "stroke-dasharray": "", "stroke-linecap": "butt", "stroke-linejoin": "butt", "stroke-miterlimit": 0, "stroke-opacity": 1, "stroke-width": 1, target: "_blank", "text-anchor": "middle", title: "Raphael", translation: "0 0", width: 0, x: 0, y: 0},
        availableAnimAttrs = {along: "along", blur: nu, "clip-rect": "csv", cx: nu, cy: nu, fill: "colour", "fill-opacity": nu, "font-size": nu, height: nu, opacity: nu, path: "path", r: nu, rotation: "csv", rx: nu, ry: nu, scale: "csv", stroke: "colour", "stroke-opacity": nu, "stroke-width": nu, translation: "csv", width: nu, x: nu, y: nu},
        rp = "replace",
        animKeyFrames= /^(from|to|\d+%?)$/,
        commaSpaces = /\s*,\s*/,
        hsrg = {hs: 1, rg: 1},
        p2s = /,?([achlmqrstvxz]),?/gi,
        pathCommand = /([achlmqstvz])[\s,]*((-?\d*\.?\d*(?:e[-+]?\d+)?\s*,?\s*)+)/ig,
        pathValues = /(-?\d*\.?\d*(?:e[-+]?\d+)?)\s*,?\s*/ig,
        radial_gradient = /^r(?:\(([^,]+?)\s*,\s*([^\)]+?)\))?/,
        sortByKey = function (a, b) {
            return a.key - b.key;
        };

    R.type = (win.SVGAngle || doc.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#BasicStructure", "1.1") ? "SVG" : "VML");
    if (R.type == "VML") {
        var d = doc.createElement("div"),
            b;
        d.innerHTML = '<v:shape adj="1"/>';
        b = d.firstChild;
        b.style.behavior = "url(#default#VML)";
        if (!(b && typeof b.adj == "object")) {
            return R.type = null;
        }
        d = null;
    }
    R.svg = !(R.vml = R.type == "VML");
    Paper[proto] = R[proto];
    paperproto = Paper[proto];
    R._id = 0;
    R._oid = 0;
    R.fn = {};
    R.is = function (o, type) {
        type = lowerCase.call(type);
        if (type == "finite") {
            return !isnan[has](+o);
        }
        return  (type == "null" && o === null) ||
                (type == typeof o) ||
                (type == "object" && o === Object(o)) ||
                (type == "array" && Array.isArray && Array.isArray(o)) ||
                objectToString.call(o).slice(8, -1).toLowerCase() == type;
    };
    R.angle = function (x1, y1, x2, y2, x3, y3) {
        if (x3 == null) {
            var x = x1 - x2,
                y = y1 - y2;
            if (!x && !y) {
                return 0;
            }
            return ((x < 0) * 180 + math.atan(-y / -x) * 180 / PI + 360) % 360;
        } else {
            return R.angle(x1, y1, x3, y3) - R.angle(x2, y2, x3, y3);
        }
    };
    R.rad = function (deg) {
        return deg % 360 * PI / 180;
    };
    R.deg = function (rad) {
        return rad * 180 / PI % 360;
    };
    R.snapTo = function (values, value, tolerance) {
        tolerance = R.is(tolerance, "finite") ? tolerance : 10;
        if (R.is(values, array)) {
            var i = values.length;
            while (i--) if (abs(values[i] - value) <= tolerance) {
                return values[i];
            }
        } else {
            values = +values;
            var rem = value % values;
            if (rem < tolerance) {
                return value - rem;
            }
            if (rem > values - tolerance) {
                return value - rem + values;
            }
        }
        return value;
    };
    function createUUID() {
        // http://www.ietf.org/rfc/rfc4122.txt
        var s = [],
            i = 0;
        for (; i < 32; i++) {
            s[i] = (~~(math.random() * 16))[toString](16);
        }
        s[12] = 4;  // bits 12-15 of the time_hi_and_version field to 0010
        s[16] = ((s[16] & 3) | 8)[toString](16);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
        return "r-" + s[join]("");
    }

    R.setWindow = function (newwin) {
        win = newwin;
        doc = win.document;
    };
    // colour utilities
    var toHex = function (color) {
        if (R.vml) {
            // http://dean.edwards.name/weblog/2009/10/convert-any-colour-value-to-hex-in-msie/
            var trim = /^\s+|\s+$/g;
            var bod;
            try {
                var docum = new ActiveXObject("htmlfile");
                docum.write("<body>");
                docum.close();
                bod = docum.body;
            } catch(e) {
                bod = createPopup().document.body;
            }
            var range = bod.createTextRange();
            toHex = cacher(function (color) {
                try {
                    bod.style.color = Str(color)[rp](trim, E);
                    var value = range.queryCommandValue("ForeColor");
                    value = ((value & 255) << 16) | (value & 65280) | ((value & 16711680) >>> 16);
                    return "#" + ("000000" + value[toString](16)).slice(-6);
                } catch(e) {
                    return "none";
                }
            });
        } else {
            var i = doc.createElement("i");
            i.title = "Rapha\xebl Colour Picker";
            i.style.display = "none";
            doc.body[appendChild](i);
            toHex = cacher(function (color) {
                i.style.color = color;
                return doc.defaultView.getComputedStyle(i, E).getPropertyValue("color");
            });
        }
        return toHex(color);
    },
    hsbtoString = function () {
        return "hsb(" + [this.h, this.s, this.b] + ")";
    },
    hsltoString = function () {
        return "hsl(" + [this.h, this.s, this.l] + ")";
    },
    rgbtoString = function () {
        return this.hex;
    };
    R.hsb2rgb = function (h, s, b, o) {
        if (R.is(h, "object") && "h" in h && "s" in h && "b" in h) {
            b = h.b;
            s = h.s;
            h = h.h;
            o = h.o;
        }
        return R.hsl2rgb(h, s, b / 2, o);
    };
    R.hsl2rgb = function (h, s, l, o) {
        if (R.is(h, "object") && "h" in h && "s" in h && "l" in h) {
            l = h.l;
            s = h.s;
            h = h.h;
        }
        if (h > 1 || s > 1 || l > 1) {
            h /= 360;
            s /= 100;
            l /= 100;
        }
        var rgb = {},
            channels = ["r", "g", "b"],
            t2, t1, t3, r, g, b;
        if (!s) {
            rgb = {
                r: l,
                g: l,
                b: l
            };
        } else {
            if (l < .5) {
                t2 = l * (1 + s);
            } else {
                t2 = l + s - l * s;
            }
            t1 = 2 * l - t2;
            for (var i = 0; i < 3; i++) {
                t3 = h + 1 / 3 * -(i - 1);
                t3 < 0 && t3++;
                t3 > 1 && t3--;
                if (t3 * 6 < 1) {
                    rgb[channels[i]] = t1 + (t2 - t1) * 6 * t3;
                } else if (t3 * 2 < 1) {
                    rgb[channels[i]] = t2;
                } else if (t3 * 3 < 2) {
                    rgb[channels[i]] = t1 + (t2 - t1) * (2 / 3 - t3) * 6;
                } else {
                    rgb[channels[i]] = t1;
                }
            }
        }
        rgb.r *= 255;
        rgb.g *= 255;
        rgb.b *= 255;
        rgb.hex = "#" + (16777216 | rgb.b | (rgb.g << 8) | (rgb.r << 16)).toString(16).slice(1);
        R.is(o, "finite") && (rgb.opacity = o);
        rgb.toString = rgbtoString;
        return rgb;
    };
    R.rgb2hsb = function (red, green, blue) {
        if (green == null && R.is(red, "object") && "r" in red && "g" in red && "b" in red) {
            blue = red.b;
            green = red.g;
            red = red.r;
        }
        if (green == null && R.is(red, string)) {
            var clr = R.getRGB(red);
            red = clr.r;
            green = clr.g;
            blue = clr.b;
        }
        if (red > 1 || green > 1 || blue > 1) {
            red /= 255;
            green /= 255;
            blue /= 255;
        }
        var max = mmax(red, green, blue),
            min = mmin(red, green, blue),
            hue,
            saturation,
            brightness = max;
        if (min == max) {
            return {h: 0, s: 0, b: max, toString: hsbtoString};
        } else {
            var delta = (max - min);
            saturation = delta / max;
            if (red == max) {
                hue = (green - blue) / delta;
            } else if (green == max) {
                hue = 2 + ((blue - red) / delta);
            } else {
                hue = 4 + ((red - green) / delta);
            }
            hue /= 6;
            hue < 0 && hue++;
            hue > 1 && hue--;
        }
        return {h: hue, s: saturation, b: brightness, toString: hsbtoString};
    };
    R.rgb2hsl = function (red, green, blue) {
        if (green == null && R.is(red, "object") && "r" in red && "g" in red && "b" in red) {
            blue = red.b;
            green = red.g;
            red = red.r;
        }
        if (green == null && R.is(red, string)) {
            var clr = R.getRGB(red);
            red = clr.r;
            green = clr.g;
            blue = clr.b;
        }
        if (red > 1 || green > 1 || blue > 1) {
            red /= 255;
            green /= 255;
            blue /= 255;
        }
        var max = mmax(red, green, blue),
            min = mmin(red, green, blue),
            h,
            s,
            l = (max + min) / 2,
            hsl;
        if (min == max) {
            hsl =  {h: 0, s: 0, l: l};
        } else {
            var delta = max - min;
            s = l < .5 ? delta / (max + min) : delta / (2 - max - min);
            if (red == max) {
                h = (green - blue) / delta;
            } else if (green == max) {
                h = 2 + (blue - red) / delta;
            } else {
                h = 4 + (red - green) / delta;
            }
            h /= 6;
            h < 0 && h++;
            h > 1 && h--;
            hsl = {h: h, s: s, l: l};
        }
        hsl.toString = hsltoString;
        return hsl;
    };
    R._path2string = function () {
        return this.join(",")[rp](p2s, "$1");
    };
    function cacher(f, scope, postprocessor) {
        function newf() {
            var arg = Array[proto].slice.call(arguments, 0),
                args = arg[join]("\u25ba"),
                cache = newf.cache = newf.cache || {},
                count = newf.count = newf.count || [];
            if (cache[has](args)) {
                return postprocessor ? postprocessor(cache[args]) : cache[args];
            }
            count[length] >= 1e3 && delete cache[count.shift()];
            count[push](args);
            cache[args] = f[apply](scope, arg);
            return postprocessor ? postprocessor(cache[args]) : cache[args];
        }
        return newf;
    }
 
    R.getRGB = cacher(function (colour) {
        if (!colour || !!((colour = Str(colour)).indexOf("-") + 1)) {
            return {r: -1, g: -1, b: -1, hex: "none", error: 1};
        }
        if (colour == "none") {
            return {r: -1, g: -1, b: -1, hex: "none"};
        }
        !(hsrg[has](colour.toLowerCase().substring(0, 2)) || colour.charAt() == "#") && (colour = toHex(colour));
        var res,
            red,
            green,
            blue,
            opacity,
            t,
            values,
            rgb = colour.match(colourRegExp);
        if (rgb) {
            if (rgb[2]) {
                blue = toInt(rgb[2].substring(5), 16);
                green = toInt(rgb[2].substring(3, 5), 16);
                red = toInt(rgb[2].substring(1, 3), 16);
            }
            if (rgb[3]) {
                blue = toInt((t = rgb[3].charAt(3)) + t, 16);
                green = toInt((t = rgb[3].charAt(2)) + t, 16);
                red = toInt((t = rgb[3].charAt(1)) + t, 16);
            }
            if (rgb[4]) {
                values = rgb[4][split](commaSpaces);
                red = toFloat(values[0]);
                values[0].slice(-1) == "%" && (red *= 2.55);
                green = toFloat(values[1]);
                values[1].slice(-1) == "%" && (green *= 2.55);
                blue = toFloat(values[2]);
                values[2].slice(-1) == "%" && (blue *= 2.55);
                rgb[1].toLowerCase().slice(0, 4) == "rgba" && (opacity = toFloat(values[3]));
                values[3] && values[3].slice(-1) == "%" && (opacity /= 100);
            }
            if (rgb[5]) {
                values = rgb[5][split](commaSpaces);
                red = toFloat(values[0]);
                values[0].slice(-1) == "%" && (red *= 2.55);
                green = toFloat(values[1]);
                values[1].slice(-1) == "%" && (green *= 2.55);
                blue = toFloat(values[2]);
                values[2].slice(-1) == "%" && (blue *= 2.55);
                (values[0].slice(-3) == "deg" || values[0].slice(-1) == "\xb0") && (red /= 360);
                rgb[1].toLowerCase().slice(0, 4) == "hsba" && (opacity = toFloat(values[3]));
                values[3] && values[3].slice(-1) == "%" && (opacity /= 100);
                return R.hsb2rgb(red, green, blue, opacity);
            }
            if (rgb[6]) {
                values = rgb[6][split](commaSpaces);
                red = toFloat(values[0]);
                values[0].slice(-1) == "%" && (red *= 2.55);
                green = toFloat(values[1]);
                values[1].slice(-1) == "%" && (green *= 2.55);
                blue = toFloat(values[2]);
                values[2].slice(-1) == "%" && (blue *= 2.55);
                (values[0].slice(-3) == "deg" || values[0].slice(-1) == "\xb0") && (red /= 360);
                rgb[1].toLowerCase().slice(0, 4) == "hsla" && (opacity = toFloat(values[3]));
                values[3] && values[3].slice(-1) == "%" && (opacity /= 100);
                return R.hsl2rgb(red, green, blue, opacity);
            }
            rgb = {r: red, g: green, b: blue};
            rgb.hex = "#" + (16777216 | blue | (green << 8) | (red << 16)).toString(16).slice(1);
            R.is(opacity, "finite") && (rgb.opacity = opacity);
            return rgb;
        }
        return {r: -1, g: -1, b: -1, hex: "none", error: 1};
    }, R);
    R.getColor = function (value) {
        var start = this.getColor.start = this.getColor.start || {h: 0, s: 1, b: value || .75},
            rgb = this.hsb2rgb(start.h, start.s, start.b);
        start.h += .075;
        if (start.h > 1) {
            start.h = 0;
            start.s -= .2;
            start.s <= 0 && (this.getColor.start = {h: 0, s: 1, b: start.b});
        }
        return rgb.hex;
    };
    R.getColor.reset = function () {
        delete this.start;
    };
    // path utilities
    R.parsePathString = cacher(function (pathString) {
        if (!pathString) {
            return null;
        }
        var paramCounts = {a: 7, c: 6, h: 1, l: 2, m: 2, q: 4, s: 4, t: 2, v: 1, z: 0},
            data = [];
        if (R.is(pathString, array) && R.is(pathString[0], array)) { // rough assumption
            data = pathClone(pathString);
        }
        if (!data[length]) {
            Str(pathString)[rp](pathCommand, function (a, b, c) {
                var params = [],
                    name = lowerCase.call(b);
                c[rp](pathValues, function (a, b) {
                    b && params[push](+b);
                });
                if (name == "m" && params[length] > 2) {
                    data[push]([b][concat](params.splice(0, 2)));
                    name = "l";
                    b = b == "m" ? "l" : "L";
                }
                while (params[length] >= paramCounts[name]) {
                    data[push]([b][concat](params.splice(0, paramCounts[name])));
                    if (!paramCounts[name]) {
                        break;
                    }
                }
            });
        }
        data[toString] = R._path2string;
        return data;
    });
    R.findDotsAtSegment = function (p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t) {
        var t1 = 1 - t,
            x = pow(t1, 3) * p1x + pow(t1, 2) * 3 * t * c1x + t1 * 3 * t * t * c2x + pow(t, 3) * p2x,
            y = pow(t1, 3) * p1y + pow(t1, 2) * 3 * t * c1y + t1 * 3 * t * t * c2y + pow(t, 3) * p2y,
            mx = p1x + 2 * t * (c1x - p1x) + t * t * (c2x - 2 * c1x + p1x),
            my = p1y + 2 * t * (c1y - p1y) + t * t * (c2y - 2 * c1y + p1y),
            nx = c1x + 2 * t * (c2x - c1x) + t * t * (p2x - 2 * c2x + c1x),
            ny = c1y + 2 * t * (c2y - c1y) + t * t * (p2y - 2 * c2y + c1y),
            ax = (1 - t) * p1x + t * c1x,
            ay = (1 - t) * p1y + t * c1y,
            cx = (1 - t) * c2x + t * p2x,
            cy = (1 - t) * c2y + t * p2y,
            alpha = (90 - math.atan((mx - nx) / (my - ny)) * 180 / PI);
        (mx > nx || my < ny) && (alpha += 180);
        return {x: x, y: y, m: {x: mx, y: my}, n: {x: nx, y: ny}, start: {x: ax, y: ay}, end: {x: cx, y: cy}, alpha: alpha};
    };
    var pathDimensions = cacher(function (path) {
        if (!path) {
            return {x: 0, y: 0, width: 0, height: 0};
        }
        path = path2curve(path);
        var x = 0, 
            y = 0,
            X = [],
            Y = [],
            p;
        for (var i = 0, ii = path[length]; i < ii; i++) {
            p = path[i];
            if (p[0] == "M") {
                x = p[1];
                y = p[2];
                X[push](x);
                Y[push](y);
            } else {
                var dim = curveDim(x, y, p[1], p[2], p[3], p[4], p[5], p[6]);
                X = X[concat](dim.min.x, dim.max.x);
                Y = Y[concat](dim.min.y, dim.max.y);
                x = p[5];
                y = p[6];
            }
        }
        var xmin = mmin[apply](0, X),
            ymin = mmin[apply](0, Y);
        return {
            x: xmin,
            y: ymin,
            width: mmax[apply](0, X) - xmin,
            height: mmax[apply](0, Y) - ymin
        };
    }),
        pathClone = function (pathArray) {
            var res = [];
            if (!R.is(pathArray, array) || !R.is(pathArray && pathArray[0], array)) { // rough assumption
                pathArray = R.parsePathString(pathArray);
            }
            for (var i = 0, ii = pathArray[length]; i < ii; i++) {
                res[i] = [];
                for (var j = 0, jj = pathArray[i][length]; j < jj; j++) {
                    res[i][j] = pathArray[i][j];
                }
            }
            res[toString] = R._path2string;
            return res;
        },
        pathToRelative = cacher(function (pathArray) {
            if (!R.is(pathArray, array) || !R.is(pathArray && pathArray[0], array)) { // rough assumption
                pathArray = R.parsePathString(pathArray);
            }
            var res = [],
                x = 0,
                y = 0,
                mx = 0,
                my = 0,
                start = 0;
            if (pathArray[0][0] == "M") {
                x = pathArray[0][1];
                y = pathArray[0][2];
                mx = x;
                my = y;
                start++;
                res[push](["M", x, y]);
            }
            for (var i = start, ii = pathArray[length]; i < ii; i++) {
                var r = res[i] = [],
                    pa = pathArray[i];
                if (pa[0] != lowerCase.call(pa[0])) {
                    r[0] = lowerCase.call(pa[0]);
                    switch (r[0]) {
                        case "a":
                            r[1] = pa[1];
                            r[2] = pa[2];
                            r[3] = pa[3];
                            r[4] = pa[4];
                            r[5] = pa[5];
                            r[6] = +(pa[6] - x).toFixed(3);
                            r[7] = +(pa[7] - y).toFixed(3);
                            break;
                        case "v":
                            r[1] = +(pa[1] - y).toFixed(3);
                            break;
                        case "m":
                            mx = pa[1];
                            my = pa[2];
                        default:
                            for (var j = 1, jj = pa[length]; j < jj; j++) {
                                r[j] = +(pa[j] - ((j % 2) ? x : y)).toFixed(3);
                            }
                    }
                } else {
                    r = res[i] = [];
                    if (pa[0] == "m") {
                        mx = pa[1] + x;
                        my = pa[2] + y;
                    }
                    for (var k = 0, kk = pa[length]; k < kk; k++) {
                        res[i][k] = pa[k];
                    }
                }
                var len = res[i][length];
                switch (res[i][0]) {
                    case "z":
                        x = mx;
                        y = my;
                        break;
                    case "h":
                        x += +res[i][len - 1];
                        break;
                    case "v":
                        y += +res[i][len - 1];
                        break;
                    default:
                        x += +res[i][len - 2];
                        y += +res[i][len - 1];
                }
            }
            res[toString] = R._path2string;
            return res;
        }, 0, pathClone),
        pathToAbsolute = cacher(function (pathArray) {
            if (!R.is(pathArray, array) || !R.is(pathArray && pathArray[0], array)) { // rough assumption
                pathArray = R.parsePathString(pathArray);
            }
            var res = [],
                x = 0,
                y = 0,
                mx = 0,
                my = 0,
                start = 0;
            if (pathArray[0][0] == "M") {
                x = +pathArray[0][1];
                y = +pathArray[0][2];
                mx = x;
                my = y;
                start++;
                res[0] = ["M", x, y];
            }
            for (var i = start, ii = pathArray[length]; i < ii; i++) {
                var r = res[i] = [],
                    pa = pathArray[i];
                if (pa[0] != upperCase.call(pa[0])) {
                    r[0] = upperCase.call(pa[0]);
                    switch (r[0]) {
                        case "A":
                            r[1] = pa[1];
                            r[2] = pa[2];
                            r[3] = pa[3];
                            r[4] = pa[4];
                            r[5] = pa[5];
                            r[6] = +(pa[6] + x);
                            r[7] = +(pa[7] + y);
                            break;
                        case "V":
                            r[1] = +pa[1] + y;
                            break;
                        case "H":
                            r[1] = +pa[1] + x;
                            break;
                        case "M":
                            mx = +pa[1] + x;
                            my = +pa[2] + y;
                        default:
                            for (var j = 1, jj = pa[length]; j < jj; j++) {
                                r[j] = +pa[j] + ((j % 2) ? x : y);
                            }
                    }
                } else {
                    for (var k = 0, kk = pa[length]; k < kk; k++) {
                        res[i][k] = pa[k];
                    }
                }
                switch (r[0]) {
                    case "Z":
                        x = mx;
                        y = my;
                        break;
                    case "H":
                        x = r[1];
                        break;
                    case "V":
                        y = r[1];
                        break;
                    case "M":
                        mx = res[i][res[i][length] - 2];
                        my = res[i][res[i][length] - 1];
                    default:
                        x = res[i][res[i][length] - 2];
                        y = res[i][res[i][length] - 1];
                }
            }
            res[toString] = R._path2string;
            return res;
        }, null, pathClone),
        l2c = function (x1, y1, x2, y2) {
            return [x1, y1, x2, y2, x2, y2];
        },
        q2c = function (x1, y1, ax, ay, x2, y2) {
            var _13 = 1 / 3,
                _23 = 2 / 3;
            return [
                    _13 * x1 + _23 * ax,
                    _13 * y1 + _23 * ay,
                    _13 * x2 + _23 * ax,
                    _13 * y2 + _23 * ay,
                    x2,
                    y2
                ];
        },
        a2c = function (x1, y1, rx, ry, angle, large_arc_flag, sweep_flag, x2, y2, recursive) {
            // for more information of where this math came from visit:
            // http://www.w3.org/TR/SVG11/implnote.html#ArcImplementationNotes
            var _120 = PI * 120 / 180,
                rad = PI / 180 * (+angle || 0),
                res = [],
                xy,
                rotate = cacher(function (x, y, rad) {
                    var X = x * math.cos(rad) - y * math.sin(rad),
                        Y = x * math.sin(rad) + y * math.cos(rad);
                    return {x: X, y: Y};
                });
            if (!recursive) {
                xy = rotate(x1, y1, -rad);
                x1 = xy.x;
                y1 = xy.y;
                xy = rotate(x2, y2, -rad);
                x2 = xy.x;
                y2 = xy.y;
                var cos = math.cos(PI / 180 * angle),
                    sin = math.sin(PI / 180 * angle),
                    x = (x1 - x2) / 2,
                    y = (y1 - y2) / 2;
                var h = (x * x) / (rx * rx) + (y * y) / (ry * ry);
                if (h > 1) {
                    h = math.sqrt(h);
                    rx = h * rx;
                    ry = h * ry;
                }
                var rx2 = rx * rx,
                    ry2 = ry * ry,
                    k = (large_arc_flag == sweep_flag ? -1 : 1) *
                        math.sqrt(abs((rx2 * ry2 - rx2 * y * y - ry2 * x * x) / (rx2 * y * y + ry2 * x * x))),
                    cx = k * rx * y / ry + (x1 + x2) / 2,
                    cy = k * -ry * x / rx + (y1 + y2) / 2,
                    f1 = math.asin(((y1 - cy) / ry).toFixed(9)),
                    f2 = math.asin(((y2 - cy) / ry).toFixed(9));

                f1 = x1 < cx ? PI - f1 : f1;
                f2 = x2 < cx ? PI - f2 : f2;
                f1 < 0 && (f1 = PI * 2 + f1);
                f2 < 0 && (f2 = PI * 2 + f2);
                if (sweep_flag && f1 > f2) {
                    f1 = f1 - PI * 2;
                }
                if (!sweep_flag && f2 > f1) {
                    f2 = f2 - PI * 2;
                }
            } else {
                f1 = recursive[0];
                f2 = recursive[1];
                cx = recursive[2];
                cy = recursive[3];
            }
            var df = f2 - f1;
            if (abs(df) > _120) {
                var f2old = f2,
                    x2old = x2,
                    y2old = y2;
                f2 = f1 + _120 * (sweep_flag && f2 > f1 ? 1 : -1);
                x2 = cx + rx * math.cos(f2);
                y2 = cy + ry * math.sin(f2);
                res = a2c(x2, y2, rx, ry, angle, 0, sweep_flag, x2old, y2old, [f2, f2old, cx, cy]);
            }
            df = f2 - f1;
            var c1 = math.cos(f1),
                s1 = math.sin(f1),
                c2 = math.cos(f2),
                s2 = math.sin(f2),
                t = math.tan(df / 4),
                hx = 4 / 3 * rx * t,
                hy = 4 / 3 * ry * t,
                m1 = [x1, y1],
                m2 = [x1 + hx * s1, y1 - hy * c1],
                m3 = [x2 + hx * s2, y2 - hy * c2],
                m4 = [x2, y2];
            m2[0] = 2 * m1[0] - m2[0];
            m2[1] = 2 * m1[1] - m2[1];
            if (recursive) {
                return [m2, m3, m4][concat](res);
            } else {
                res = [m2, m3, m4][concat](res)[join]()[split](",");
                var newres = [];
                for (var i = 0, ii = res[length]; i < ii; i++) {
                    newres[i] = i % 2 ? rotate(res[i - 1], res[i], rad).y : rotate(res[i], res[i + 1], rad).x;
                }
                return newres;
            }
        },
        findDotAtSegment = function (p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t) {
            var t1 = 1 - t;
            return {
                x: pow(t1, 3) * p1x + pow(t1, 2) * 3 * t * c1x + t1 * 3 * t * t * c2x + pow(t, 3) * p2x,
                y: pow(t1, 3) * p1y + pow(t1, 2) * 3 * t * c1y + t1 * 3 * t * t * c2y + pow(t, 3) * p2y
            };
        },
        curveDim = cacher(function (p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y) {
            var a = (c2x - 2 * c1x + p1x) - (p2x - 2 * c2x + c1x),
                b = 2 * (c1x - p1x) - 2 * (c2x - c1x),
                c = p1x - c1x,
                t1 = (-b + math.sqrt(b * b - 4 * a * c)) / 2 / a,
                t2 = (-b - math.sqrt(b * b - 4 * a * c)) / 2 / a,
                y = [p1y, p2y],
                x = [p1x, p2x],
                dot;
            abs(t1) > "1e12" && (t1 = .5);
            abs(t2) > "1e12" && (t2 = .5);
            if (t1 > 0 && t1 < 1) {
                dot = findDotAtSegment(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t1);
                x[push](dot.x);
                y[push](dot.y);
            }
            if (t2 > 0 && t2 < 1) {
                dot = findDotAtSegment(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t2);
                x[push](dot.x);
                y[push](dot.y);
            }
            a = (c2y - 2 * c1y + p1y) - (p2y - 2 * c2y + c1y);
            b = 2 * (c1y - p1y) - 2 * (c2y - c1y);
            c = p1y - c1y;
            t1 = (-b + math.sqrt(b * b - 4 * a * c)) / 2 / a;
            t2 = (-b - math.sqrt(b * b - 4 * a * c)) / 2 / a;
            abs(t1) > "1e12" && (t1 = .5);
            abs(t2) > "1e12" && (t2 = .5);
            if (t1 > 0 && t1 < 1) {
                dot = findDotAtSegment(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t1);
                x[push](dot.x);
                y[push](dot.y);
            }
            if (t2 > 0 && t2 < 1) {
                dot = findDotAtSegment(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, t2);
                x[push](dot.x);
                y[push](dot.y);
            }
            return {
                min: {x: mmin[apply](0, x), y: mmin[apply](0, y)},
                max: {x: mmax[apply](0, x), y: mmax[apply](0, y)}
            };
        }),
        path2curve = cacher(function (path, path2) {
            var p = pathToAbsolute(path),
                p2 = path2 && pathToAbsolute(path2),
                attrs = {x: 0, y: 0, bx: 0, by: 0, X: 0, Y: 0, qx: null, qy: null},
                attrs2 = {x: 0, y: 0, bx: 0, by: 0, X: 0, Y: 0, qx: null, qy: null},
                processPath = function (path, d) {
                    var nx, ny;
                    if (!path) {
                        return ["C", d.x, d.y, d.x, d.y, d.x, d.y];
                    }
                    !(path[0] in {T:1, Q:1}) && (d.qx = d.qy = null);
                    switch (path[0]) {
                        case "M":
                            d.X = path[1];
                            d.Y = path[2];
                            break;
                        case "A":
                            path = ["C"][concat](a2c[apply](0, [d.x, d.y][concat](path.slice(1))));
                            break;
                        case "S":
                            nx = d.x + (d.x - (d.bx || d.x));
                            ny = d.y + (d.y - (d.by || d.y));
                            path = ["C", nx, ny][concat](path.slice(1));
                            break;
                        case "T":
                            d.qx = d.x + (d.x - (d.qx || d.x));
                            d.qy = d.y + (d.y - (d.qy || d.y));
                            path = ["C"][concat](q2c(d.x, d.y, d.qx, d.qy, path[1], path[2]));
                            break;
                        case "Q":
                            d.qx = path[1];
                            d.qy = path[2];
                            path = ["C"][concat](q2c(d.x, d.y, path[1], path[2], path[3], path[4]));
                            break;
                        case "L":
                            path = ["C"][concat](l2c(d.x, d.y, path[1], path[2]));
                            break;
                        case "H":
                            path = ["C"][concat](l2c(d.x, d.y, path[1], d.y));
                            break;
                        case "V":
                            path = ["C"][concat](l2c(d.x, d.y, d.x, path[1]));
                            break;
                        case "Z":
                            path = ["C"][concat](l2c(d.x, d.y, d.X, d.Y));
                            break;
                    }
                    return path;
                },
                fixArc = function (pp, i) {
                    if (pp[i][length] > 7) {
                        pp[i].shift();
                        var pi = pp[i];
                        while (pi[length]) {
                            pp.splice(i++, 0, ["C"][concat](pi.splice(0, 6)));
                        }
                        pp.splice(i, 1);
                        ii = mmax(p[length], p2 && p2[length] || 0);
                    }
                },
                fixM = function (path1, path2, a1, a2, i) {
                    if (path1 && path2 && path1[i][0] == "M" && path2[i][0] != "M") {
                        path2.splice(i, 0, ["M", a2.x, a2.y]);
                        a1.bx = 0;
                        a1.by = 0;
                        a1.x = path1[i][1];
                        a1.y = path1[i][2];
                        ii = mmax(p[length], p2 && p2[length] || 0);
                    }
                };
            for (var i = 0, ii = mmax(p[length], p2 && p2[length] || 0); i < ii; i++) {
                p[i] = processPath(p[i], attrs);
                fixArc(p, i);
                p2 && (p2[i] = processPath(p2[i], attrs2));
                p2 && fixArc(p2, i);
                fixM(p, p2, attrs, attrs2, i);
                fixM(p2, p, attrs2, attrs, i);
                var seg = p[i],
                    seg2 = p2 && p2[i],
                    seglen = seg[length],
                    seg2len = p2 && seg2[length];
                attrs.x = seg[seglen - 2];
                attrs.y = seg[seglen - 1];
                attrs.bx = toFloat(seg[seglen - 4]) || attrs.x;
                attrs.by = toFloat(seg[seglen - 3]) || attrs.y;
                attrs2.bx = p2 && (toFloat(seg2[seg2len - 4]) || attrs2.x);
                attrs2.by = p2 && (toFloat(seg2[seg2len - 3]) || attrs2.y);
                attrs2.x = p2 && seg2[seg2len - 2];
                attrs2.y = p2 && seg2[seg2len - 1];
            }
            return p2 ? [p, p2] : p;
        }, null, pathClone),
        parseDots = cacher(function (gradient) {
            var dots = [];
            for (var i = 0, ii = gradient[length]; i < ii; i++) {
                var dot = {},
                    par = gradient[i].match(/^([^:]*):?([\d\.]*)/);
                dot.color = R.getRGB(par[1]);
                if (dot.color.error) {
                    return null;
                }
                dot.color = dot.color.hex;
                par[2] && (dot.offset = par[2] + "%");
                dots[push](dot);
            }
            for (i = 1, ii = dots[length] - 1; i < ii; i++) {
                if (!dots[i].offset) {
                    var start = toFloat(dots[i - 1].offset || 0),
                        end = 0;
                    for (var j = i + 1; j < ii; j++) {
                        if (dots[j].offset) {
                            end = dots[j].offset;
                            break;
                        }
                    }
                    if (!end) {
                        end = 100;
                        j = ii;
                    }
                    end = toFloat(end);
                    var d = (end - start) / (j - i + 1);
                    for (; i < j; i++) {
                        start += d;
                        dots[i].offset = start + "%";
                    }
                }
            }
            return dots;
        }),
        getContainer = function (x, y, w, h) {
            var container;
            if (R.is(x, string) || R.is(x, "object")) {
                container = R.is(x, string) ? doc.getElementById(x) : x;
                if (container.tagName) {
                    if (y == null) {
                        return {
                            container: container,
                            width: container.style.pixelWidth || container.offsetWidth,
                            height: container.style.pixelHeight || container.offsetHeight
                        };
                    } else {
                        return {container: container, width: y, height: w};
                    }
                }
            } else {
                return {container: 1, x: x, y: y, width: w, height: h};
            }
        },
        plugins = function (con, add) {
            var that = this;
            for (var prop in add) {
                if (add[has](prop) && !(prop in con)) {
                    switch (typeof add[prop]) {
                        case "function":
                            (function (f) {
                                con[prop] = con === that ? f : function () { return f[apply](that, arguments); };
                            })(add[prop]);
                        break;
                        case "object":
                            con[prop] = con[prop] || {};
                            plugins.call(this, con[prop], add[prop]);
                        break;
                        default:
                            con[prop] = add[prop];
                        break;
                    }
                }
            }
        },
        tear = function (el, paper) {
            el == paper.top && (paper.top = el.prev);
            el == paper.bottom && (paper.bottom = el.next);
            el.next && (el.next.prev = el.prev);
            el.prev && (el.prev.next = el.next);
        },
        tofront = function (el, paper) {
            if (paper.top === el) {
                return;
            }
            tear(el, paper);
            el.next = null;
            el.prev = paper.top;
            paper.top.next = el;
            paper.top = el;
        },
        toback = function (el, paper) {
            if (paper.bottom === el) {
                return;
            }
            tear(el, paper);
            el.next = paper.bottom;
            el.prev = null;
            paper.bottom.prev = el;
            paper.bottom = el;
        },
        insertafter = function (el, el2, paper) {
            tear(el, paper);
            el2 == paper.top && (paper.top = el);
            el2.next && (el2.next.prev = el);
            el.next = el2.next;
            el.prev = el2;
            el2.next = el;
        },
        insertbefore = function (el, el2, paper) {
            tear(el, paper);
            el2 == paper.bottom && (paper.bottom = el);
            el2.prev && (el2.prev.next = el);
            el.prev = el2.prev;
            el2.prev = el;
            el.next = el2;
        },
        removed = function (methodname) {
            return function () {
                throw new Error("Rapha\xebl: you are calling to method \u201c" + methodname + "\u201d of removed object");
            };
        };
    R.pathToRelative = pathToRelative;
    // SVG
    if (R.svg) {
        paperproto.svgns = "http://www.w3.org/2000/svg";
        paperproto.xlink = "http://www.w3.org/1999/xlink";
        round = function (num) {
            return +num + (~~num === num) * .5;
        };
        var p$ = function (el, attr) {
            if (attr) {
                for (var key in attr) {
                    if (attr[has](key)) {
                        el[setAttribute](key, Str(attr[key]));
                    }
                }
            } else {
                el = doc.createElementNS(paperproto.svgns, el);
                el.style.webkitTapHighlightColor = "rgba(0,0,0,0)";
                return el;
            }
        };
        R[toString] = function () {
            return  "Your browser supports SVG.\nYou are running Rapha\xebl " + this.version;
        };
        var thePath = function (pathString, SVG) {
            var el = p$("path");
            SVG.canvas && SVG.canvas[appendChild](el);
            var p = new Element(el, SVG);
            p.type = "path";
            setFillAndStroke(p, {fill: "none", stroke: "#000", path: pathString});
            return p;
        };
        var addGradientFill = function (o, gradient, SVG) {
            var type = "linear",
                fx = .5, fy = .5,
                s = o.style;
            gradient = Str(gradient)[rp](radial_gradient, function (all, _fx, _fy) {
                type = "radial";
                if (_fx && _fy) {
                    fx = toFloat(_fx);
                    fy = toFloat(_fy);
                    var dir = ((fy > .5) * 2 - 1);
                    pow(fx - .5, 2) + pow(fy - .5, 2) > .25 &&
                        (fy = math.sqrt(.25 - pow(fx - .5, 2)) * dir + .5) &&
                        fy != .5 &&
                        (fy = fy.toFixed(5) - 1e-5 * dir);
                }
                return E;
            });
            gradient = gradient[split](/\s*\-\s*/);
            if (type == "linear") {
                var angle = gradient.shift();
                angle = -toFloat(angle);
                if (isNaN(angle)) {
                    return null;
                }
                var vector = [0, 0, math.cos(angle * PI / 180), math.sin(angle * PI / 180)],
                    max = 1 / (mmax(abs(vector[2]), abs(vector[3])) || 1);
                vector[2] *= max;
                vector[3] *= max;
                if (vector[2] < 0) {
                    vector[0] = -vector[2];
                    vector[2] = 0;
                }
                if (vector[3] < 0) {
                    vector[1] = -vector[3];
                    vector[3] = 0;
                }
            }
            var dots = parseDots(gradient);
            if (!dots) {
                return null;
            }
            var id = o.getAttribute(fillString);
            id = id.match(/^url\(#(.*)\)$/);
            id && SVG.defs.removeChild(doc.getElementById(id[1]));

            var el = p$(type + "Gradient");
            el.id = createUUID();
            p$(el, type == "radial" ? {fx: fx, fy: fy} : {x1: vector[0], y1: vector[1], x2: vector[2], y2: vector[3]});
            SVG.defs[appendChild](el);
            for (var i = 0, ii = dots[length]; i < ii; i++) {
                var stop = p$("stop");
                p$(stop, {
                    offset: dots[i].offset ? dots[i].offset : !i ? "0%" : "100%",
                    "stop-color": dots[i].color || "#fff"
                });
                el[appendChild](stop);
            }
            p$(o, {
                fill: "url(#" + el.id + ")",
                opacity: 1,
                "fill-opacity": 1
            });
            s.fill = E;
            s.opacity = 1;
            s.fillOpacity = 1;
            return 1;
        };
        var updatePosition = function (o) {
            var bbox = o.getBBox();
            p$(o.pattern, {patternTransform: R.format("translate({0},{1})", bbox.x, bbox.y)});
        };
        var setFillAndStroke = function (o, params) {
            var dasharray = {
                    "": [0],
                    "none": [0],
                    "-": [3, 1],
                    ".": [1, 1],
                    "-.": [3, 1, 1, 1],
                    "-..": [3, 1, 1, 1, 1, 1],
                    ". ": [1, 3],
                    "- ": [4, 3],
                    "--": [8, 3],
                    "- .": [4, 3, 1, 3],
                    "--.": [8, 3, 1, 3],
                    "--..": [8, 3, 1, 3, 1, 3]
                },
                node = o.node,
                attrs = o.attrs,
                rot = o.rotate(),
                addDashes = function (o, value) {
                    value = dasharray[lowerCase.call(value)];
                    if (value) {
                        var width = o.attrs["stroke-width"] || "1",
                            butt = {round: width, square: width, butt: 0}[o.attrs["stroke-linecap"] || params["stroke-linecap"]] || 0,
                            dashes = [];
                        var i = value[length];
                        while (i--) {
                            dashes[i] = value[i] * width + ((i % 2) ? 1 : -1) * butt;
                        }
                        p$(node, {"stroke-dasharray": dashes[join](",")});
                    }
                };
            params[has]("rotation") && (rot = params.rotation);
            var rotxy = Str(rot)[split](separator);
            if (!(rotxy.length - 1)) {
                rotxy = null;
            } else {
                rotxy[1] = +rotxy[1];
                rotxy[2] = +rotxy[2];
            }
            toFloat(rot) && o.rotate(0, true);
            for (var att in params) {
                if (params[has](att)) {
                    if (!availableAttrs[has](att)) {
                        continue;
                    }
                    var value = params[att];
                    attrs[att] = value;
                    switch (att) {
                        case "blur":
                            o.blur(value);
                            break;
                        case "rotation":
                            o.rotate(value, true);
                            break;
                        case "href":
                        case "title":
                        case "target":
                            var pn = node.parentNode;
                            if (lowerCase.call(pn.tagName) != "a") {
                                var hl = p$("a");
                                pn.insertBefore(hl, node);
                                hl[appendChild](node);
                                pn = hl;
                            }
                            if (att == "target" && value == "blank") {
                                pn.setAttributeNS(o.paper.xlink, "show", "new");
                            } else {
                                pn.setAttributeNS(o.paper.xlink, att, value);
                            }
                            break;
                        case "cursor":
                            node.style.cursor = value;
                            break;
                        case "clip-rect":
                            var rect = Str(value)[split](separator);
                            if (rect[length] == 4) {
                                o.clip && o.clip.parentNode.parentNode.removeChild(o.clip.parentNode);
                                var el = p$("clipPath"),
                                    rc = p$("rect");
                                el.id = createUUID();
                                p$(rc, {
                                    x: rect[0],
                                    y: rect[1],
                                    width: rect[2],
                                    height: rect[3]
                                });
                                el[appendChild](rc);
                                o.paper.defs[appendChild](el);
                                p$(node, {"clip-path": "url(#" + el.id + ")"});
                                o.clip = rc;
                            }
                            if (!value) {
                                var clip = doc.getElementById(node.getAttribute("clip-path")[rp](/(^url\(#|\)$)/g, E));
                                clip && clip.parentNode.removeChild(clip);
                                p$(node, {"clip-path": E});
                                delete o.clip;
                            }
                        break;
                        case "path":
                            if (o.type == "path") {
                                p$(node, {d: value ? attrs.path = pathToAbsolute(value) : "M0,0"});
                            }
                            break;
                        case "width":
                            node[setAttribute](att, value);
                            if (attrs.fx) {
                                att = "x";
                                value = attrs.x;
                            } else {
                                break;
                            }
                        case "x":
                            if (attrs.fx) {
                                value = -attrs.x - (attrs.width || 0);
                            }
                        case "rx":
                            if (att == "rx" && o.type == "rect") {
                                break;
                            }
                        case "cx":
                            rotxy && (att == "x" || att == "cx") && (rotxy[1] += value - attrs[att]);
                            node[setAttribute](att, value);
                            o.pattern && updatePosition(o);
                            break;
                        case "height":
                            node[setAttribute](att, value);
                            if (attrs.fy) {
                                att = "y";
                                value = attrs.y;
                            } else {
                                break;
                            }
                        case "y":
                            if (attrs.fy) {
                                value = -attrs.y - (attrs.height || 0);
                            }
                        case "ry":
                            if (att == "ry" && o.type == "rect") {
                                break;
                            }
                        case "cy":
                            rotxy && (att == "y" || att == "cy") && (rotxy[2] += value - attrs[att]);
                            node[setAttribute](att, value);
                            o.pattern && updatePosition(o);
                            break;
                        case "r":
                            if (o.type == "rect") {
                                p$(node, {rx: value, ry: value});
                            } else {
                                node[setAttribute](att, value);
                            }
                            break;
                        case "src":
                            if (o.type == "image") {
                                node.setAttributeNS(o.paper.xlink, "href", value);
                            }
                            break;
                        case "stroke-width":
                            node.style.strokeWidth = value;
                            // Need following line for Firefox
                            node[setAttribute](att, value);
                            if (attrs["stroke-dasharray"]) {
                                addDashes(o, attrs["stroke-dasharray"]);
                            }
                            break;
                        case "stroke-dasharray":
                            addDashes(o, value);
                            break;
                        case "translation":
                            var xy = Str(value)[split](separator);
                            xy[0] = +xy[0] || 0;
                            xy[1] = +xy[1] || 0;
                            if (rotxy) {
                                rotxy[1] += xy[0];
                                rotxy[2] += xy[1];
                            }
                            translate.call(o, xy[0], xy[1]);
                            break;
                        case "scale":
                            xy = Str(value)[split](separator);
                            o.scale(+xy[0] || 1, +xy[1] || +xy[0] || 1, isNaN(toFloat(xy[2])) ? null : +xy[2], isNaN(toFloat(xy[3])) ? null : +xy[3]);
                            break;
                        case fillString:
                            var isURL = Str(value).match(ISURL);
                            if (isURL) {
                                el = p$("pattern");
                                var ig = p$("image");
                                el.id = createUUID();
                                p$(el, {x: 0, y: 0, patternUnits: "userSpaceOnUse", height: 1, width: 1});
                                p$(ig, {x: 0, y: 0});
                                ig.setAttributeNS(o.paper.xlink, "href", isURL[1]);
                                el[appendChild](ig);
 
                                var img = doc.createElement("img");
                                img.style.cssText = "position:absolute;left:-9999em;top-9999em";
                                img.onload = function () {
                                    p$(el, {width: this.offsetWidth, height: this.offsetHeight});
                                    p$(ig, {width: this.offsetWidth, height: this.offsetHeight});
                                    doc.body.removeChild(this);
                                    o.paper.safari();
                                };
                                doc.body[appendChild](img);
                                img.src = isURL[1];
                                o.paper.defs[appendChild](el);
                                node.style.fill = "url(#" + el.id + ")";
                                p$(node, {fill: "url(#" + el.id + ")"});
                                o.pattern = el;
                                o.pattern && updatePosition(o);
                                break;
                            }
                            var clr = R.getRGB(value);
                            if (!clr.error) {
                                delete params.gradient;
                                delete attrs.gradient;
                                !R.is(attrs.opacity, "undefined") &&
                                    R.is(params.opacity, "undefined") &&
                                    p$(node, {opacity: attrs.opacity});
                                !R.is(attrs["fill-opacity"], "undefined") &&
                                    R.is(params["fill-opacity"], "undefined") &&
                                    p$(node, {"fill-opacity": attrs["fill-opacity"]});
                            } else if ((({circle: 1, ellipse: 1})[has](o.type) || Str(value).charAt() != "r") && addGradientFill(node, value, o.paper)) {
                                attrs.gradient = value;
                                attrs.fill = "none";
                                break;
                            }
                            clr[has]("opacity") && p$(node, {"fill-opacity": clr.opacity > 1 ? clr.opacity / 100 : clr.opacity});
                        case "stroke":
                            clr = R.getRGB(value);
                            node[setAttribute](att, clr.hex);
                            att == "stroke" && clr[has]("opacity") && p$(node, {"stroke-opacity": clr.opacity > 1 ? clr.opacity / 100 : clr.opacity});
                            break;
                        case "gradient":
                            (({circle: 1, ellipse: 1})[has](o.type) || Str(value).charAt() != "r") && addGradientFill(node, value, o.paper);
                            break;
                        case "opacity":
                            if (attrs.gradient && !attrs[has]("stroke-opacity")) {
                                p$(node, {"stroke-opacity": value > 1 ? value / 100 : value});
                            }
                            // fall
                        case "fill-opacity":
                            if (attrs.gradient) {
                                var gradient = doc.getElementById(node.getAttribute(fillString)[rp](/^url\(#|\)$/g, E));
                                if (gradient) {
                                    var stops = gradient.getElementsByTagName("stop");
                                    stops[stops[length] - 1][setAttribute]("stop-opacity", value);
                                }
                                break;
                            }
                        default:
                            att == "font-size" && (value = toInt(value, 10) + "px");
                            var cssrule = att[rp](/(\-.)/g, function (w) {
                                return upperCase.call(w.substring(1));
                            });
                            node.style[cssrule] = value;
                            // Need following line for Firefox
                            node[setAttribute](att, value);
                            break;
                    }
                }
            }
            
            tuneText(o, params);
            if (rotxy) {
                o.rotate(rotxy.join(S));
            } else {
                toFloat(rot) && o.rotate(rot, true);
            }
        };
        var leading = 1.2,
        tuneText = function (el, params) {
            if (el.type != "text" || !(params[has]("text") || params[has]("font") || params[has]("font-size") || params[has]("x") || params[has]("y"))) {
                return;
            }
            var a = el.attrs,
                node = el.node,
                fontSize = node.firstChild ? toInt(doc.defaultView.getComputedStyle(node.firstChild, E).getPropertyValue("font-size"), 10) : 10;
 
            if (params[has]("text")) {
                a.text = params.text;
                while (node.firstChild) {
                    node.removeChild(node.firstChild);
                }
                var texts = Str(params.text)[split]("\n");
                for (var i = 0, ii = texts[length]; i < ii; i++) if (texts[i]) {
                    var tspan = p$("tspan");
                    i && p$(tspan, {dy: fontSize * leading, x: a.x});
                    tspan[appendChild](doc.createTextNode(texts[i]));
                    node[appendChild](tspan);
                }
            } else {
                texts = node.getElementsByTagName("tspan");
                for (i = 0, ii = texts[length]; i < ii; i++) {
                    i && p$(texts[i], {dy: fontSize * leading, x: a.x});
                }
            }
            p$(node, {y: a.y});
            var bb = el.getBBox(),
                dif = a.y - (bb.y + bb.height / 2);
            dif && R.is(dif, "finite") && p$(node, {y: a.y + dif});
        },
        Element = function (node, svg) {
            var X = 0,
                Y = 0;
            this[0] = node;
            this.id = R._oid++;
            this.node = node;
            node.raphael = this;
            this.paper = svg;
            this.attrs = this.attrs || {};
            this.transformations = []; // rotate, translate, scale
            this._ = {
                tx: 0,
                ty: 0,
                rt: {deg: 0, cx: 0, cy: 0},
                sx: 1,
                sy: 1
            };
            !svg.bottom && (svg.bottom = this);
            this.prev = svg.top;
            svg.top && (svg.top.next = this);
            svg.top = this;
            this.next = null;
        };
        var elproto = Element[proto];
        Element[proto].rotate = function (deg, cx, cy) {
            if (this.removed) {
                return this;
            }
            if (deg == null) {
                if (this._.rt.cx) {
                    return [this._.rt.deg, this._.rt.cx, this._.rt.cy][join](S);
                }
                return this._.rt.deg;
            }
            var bbox = this.getBBox();
            deg = Str(deg)[split](separator);
            if (deg[length] - 1) {
                cx = toFloat(deg[1]);
                cy = toFloat(deg[2]);
            }
            deg = toFloat(deg[0]);
            if (cx != null && cx !== false) {
                this._.rt.deg = deg;
            } else {
                this._.rt.deg += deg;
            }
            (cy == null) && (cx = null);
            this._.rt.cx = cx;
            this._.rt.cy = cy;
            cx = cx == null ? bbox.x + bbox.width / 2 : cx;
            cy = cy == null ? bbox.y + bbox.height / 2 : cy;
            if (this._.rt.deg) {
                this.transformations[0] = R.format("rotate({0} {1} {2})", this._.rt.deg, cx, cy);
                this.clip && p$(this.clip, {transform: R.format("rotate({0} {1} {2})", -this._.rt.deg, cx, cy)});
            } else {
                this.transformations[0] = E;
                this.clip && p$(this.clip, {transform: E});
            }
            p$(this.node, {transform: this.transformations[join](S)});
            return this;
        };
        Element[proto].hide = function () {
            !this.removed && (this.node.style.display = "none");
            return this;
        };
        Element[proto].show = function () {
            !this.removed && (this.node.style.display = "");
            return this;
        };
        Element[proto].remove = function () {
            if (this.removed) {
                return;
            }
            tear(this, this.paper);
            this.node.parentNode.removeChild(this.node);
            for (var i in this) {
                delete this[i];
            }
            this.removed = true;
        };
        Element[proto].getBBox = function () {
            if (this.removed) {
                return this;
            }
            if (this.type == "path") {
                return pathDimensions(this.attrs.path);
            }
            if (this.node.style.display == "none") {
                this.show();
                var hide = true;
            }
            var bbox = {};
            try {
                bbox = this.node.getBBox();
            } catch(e) {
                // Firefox 3.0.x plays badly here
            } finally {
                bbox = bbox || {};
            }
            if (this.type == "text") {
                bbox = {x: bbox.x, y: Infinity, width: 0, height: 0};
                for (var i = 0, ii = this.node.getNumberOfChars(); i < ii; i++) {
                    var bb = this.node.getExtentOfChar(i);
                    (bb.y < bbox.y) && (bbox.y = bb.y);
                    (bb.y + bb.height - bbox.y > bbox.height) && (bbox.height = bb.y + bb.height - bbox.y);
                    (bb.x + bb.width - bbox.x > bbox.width) && (bbox.width = bb.x + bb.width - bbox.x);
                }
            }
            hide && this.hide();
            return bbox;
        };
        Element[proto].attr = function (name, value) {
            if (this.removed) {
                return this;
            }
            if (name == null) {
                var res = {};
                for (var i in this.attrs) if (this.attrs[has](i)) {
                    res[i] = this.attrs[i];
                }
                this._.rt.deg && (res.rotation = this.rotate());
                (this._.sx != 1 || this._.sy != 1) && (res.scale = this.scale());
                res.gradient && res.fill == "none" && (res.fill = res.gradient) && delete res.gradient;
                return res;
            }
            if (value == null && R.is(name, string)) {
                if (name == "translation") {
                    return translate.call(this);
                }
                if (name == "rotation") {
                    return this.rotate();
                }
                if (name == "scale") {
                    return this.scale();
                }
                if (name == fillString && this.attrs.fill == "none" && this.attrs.gradient) {
                    return this.attrs.gradient;
                }
                return this.attrs[name];
            }
            if (value == null && R.is(name, array)) {
                var values = {};
                for (var j = 0, jj = name.length; j < jj; j++) {
                    values[name[j]] = this.attr(name[j]);
                }
                return values;
            }
            if (value != null) {
                var params = {};
                params[name] = value;
            } else if (name != null && R.is(name, "object")) {
                params = name;
            }
            for (var key in this.paper.customAttributes) if (this.paper.customAttributes[has](key) && params[has](key) && R.is(this.paper.customAttributes[key], "function")) {
                var par = this.paper.customAttributes[key].apply(this, [][concat](params[key]));
                this.attrs[key] = params[key];
                for (var subkey in par) if (par[has](subkey)) {
                    params[subkey] = par[subkey];
                }
            }
            setFillAndStroke(this, params);
            return this;
        };
        Element[proto].toFront = function () {
            if (this.removed) {
                return this;
            }
            this.node.parentNode[appendChild](this.node);
            var svg = this.paper;
            svg.top != this && tofront(this, svg);
            return this;
        };
        Element[proto].toBack = function () {
            if (this.removed) {
                return this;
            }
            if (this.node.parentNode.firstChild != this.node) {
                this.node.parentNode.insertBefore(this.node, this.node.parentNode.firstChild);
                toback(this, this.paper);
                var svg = this.paper;
            }
            return this;
        };
        Element[proto].insertAfter = function (element) {
            if (this.removed) {
                return this;
            }
            var node = element.node || element[element.length - 1].node;
            if (node.nextSibling) {
                node.parentNode.insertBefore(this.node, node.nextSibling);
            } else {
                node.parentNode[appendChild](this.node);
            }
            insertafter(this, element, this.paper);
            return this;
        };
        Element[proto].insertBefore = function (element) {
            if (this.removed) {
                return this;
            }
            var node = element.node || element[0].node;
            node.parentNode.insertBefore(this.node, node);
            insertbefore(this, element, this.paper);
            return this;
        };
        Element[proto].blur = function (size) {
            // Experimental. No Safari support. Use it on your own risk.
            var t = this;
            if (+size !== 0) {
                var fltr = p$("filter"),
                    blur = p$("feGaussianBlur");
                t.attrs.blur = size;
                fltr.id = createUUID();
                p$(blur, {stdDeviation: +size || 1.5});
                fltr.appendChild(blur);
                t.paper.defs.appendChild(fltr);
                t._blur = fltr;
                p$(t.node, {filter: "url(#" + fltr.id + ")"});
            } else {
                if (t._blur) {
                    t._blur.parentNode.removeChild(t._blur);
                    delete t._blur;
                    delete t.attrs.blur;
                }
                t.node.removeAttribute("filter");
            }
        };
        var theCircle = function (svg, x, y, r) {
            var el = p$("circle");
            svg.canvas && svg.canvas[appendChild](el);
            var res = new Element(el, svg);
            res.attrs = {cx: x, cy: y, r: r, fill: "none", stroke: "#000"};
            res.type = "circle";
            p$(el, res.attrs);
            return res;
        },
        theRect = function (svg, x, y, w, h, r) {
            var el = p$("rect");
            svg.canvas && svg.canvas[appendChild](el);
            var res = new Element(el, svg);
            res.attrs = {x: x, y: y, width: w, height: h, r: r || 0, rx: r || 0, ry: r || 0, fill: "none", stroke: "#000"};
            res.type = "rect";
            p$(el, res.attrs);
            return res;
        },
        theEllipse = function (svg, x, y, rx, ry) {
            var el = p$("ellipse");
            svg.canvas && svg.canvas[appendChild](el);
            var res = new Element(el, svg);
            res.attrs = {cx: x, cy: y, rx: rx, ry: ry, fill: "none", stroke: "#000"};
            res.type = "ellipse";
            p$(el, res.attrs);
            return res;
        },
        theImage = function (svg, src, x, y, w, h) {
            var el = p$("image");
            p$(el, {x: x, y: y, width: w, height: h, preserveAspectRatio: "none"});
            el.setAttributeNS(svg.xlink, "href", src);
            svg.canvas && svg.canvas[appendChild](el);
            var res = new Element(el, svg);
            res.attrs = {x: x, y: y, width: w, height: h, src: src};
            res.type = "image";
            return res;
        },
        theText = function (svg, x, y, text) {
            var el = p$("text");
            p$(el, {x: x, y: y, "text-anchor": "middle"});
            svg.canvas && svg.canvas[appendChild](el);
            var res = new Element(el, svg);
            res.attrs = {x: x, y: y, "text-anchor": "middle", text: text, font: availableAttrs.font, stroke: "none", fill: "#000"};
            res.type = "text";
            setFillAndStroke(res, res.attrs);
            return res;
        },
        setSize = function (width, height) {
            this.width = width || this.width;
            this.height = height || this.height;
            this.canvas[setAttribute]("width", this.width);
            this.canvas[setAttribute]("height", this.height);
            return this;
        },
        create = function () {
            var con = getContainer[apply](0, arguments),
                container = con && con.container,
                x = con.x,
                y = con.y,
                width = con.width,
                height = con.height;
            if (!container) {
                throw new Error("SVG container not found.");
            }
            var cnvs = p$("svg");
            x = x || 0;
            y = y || 0;
            width = width || 512;
            height = height || 342;
            p$(cnvs, {
                xmlns: "http://www.w3.org/2000/svg",
                version: 1.1,
                width: width,
                height: height
            });
            if (container == 1) {
                cnvs.style.cssText = "position:absolute;left:" + x + "px;top:" + y + "px";
                doc.body[appendChild](cnvs);
            } else {
                if (container.firstChild) {
                    container.insertBefore(cnvs, container.firstChild);
                } else {
                    container[appendChild](cnvs);
                }
            }
            container = new Paper;
            container.width = width;
            container.height = height;
            container.canvas = cnvs;
            plugins.call(container, container, R.fn);
            container.clear();
            return container;
        };
        paperproto.clear = function () {
            var c = this.canvas;
            while (c.firstChild) {
                c.removeChild(c.firstChild);
            }
            this.bottom = this.top = null;
            (this.desc = p$("desc"))[appendChild](doc.createTextNode("Created with Rapha\xebl"));
            c[appendChild](this.desc);
            c[appendChild](this.defs = p$("defs"));
        };
        paperproto.remove = function () {
            this.canvas.parentNode && this.canvas.parentNode.removeChild(this.canvas);
            for (var i in this) {
                this[i] = removed(i);
            }
        };
    }

    // VML
    if (R.vml) {
        var map = {M: "m", L: "l", C: "c", Z: "x", m: "t", l: "r", c: "v", z: "x"},
            bites = /([clmz]),?([^clmz]*)/gi,
            blurregexp = / progid:\S+Blur\([^\)]+\)/g,
            val = /-?[^,\s-]+/g,
            coordsize = 1e3 + S + 1e3,
            zoom = 10,
            pathlike = {path: 1, rect: 1},
            path2vml = function (path) {
                var total =  /[ahqstv]/ig,
                    command = pathToAbsolute;
                Str(path).match(total) && (command = path2curve);
                total = /[clmz]/g;
                if (command == pathToAbsolute && !Str(path).match(total)) {
                    var res = Str(path)[rp](bites, function (all, command, args) {
                        var vals = [],
                            isMove = lowerCase.call(command) == "m",
                            res = map[command];
                        args[rp](val, function (value) {
                            if (isMove && vals[length] == 2) {
                                res += vals + map[command == "m" ? "l" : "L"];
                                vals = [];
                            }
                            vals[push](round(value * zoom));
                        });
                        return res + vals;
                    });
                    return res;
                }
                var pa = command(path), p, r;
                res = [];
                for (var i = 0, ii = pa[length]; i < ii; i++) {
                    p = pa[i];
                    r = lowerCase.call(pa[i][0]);
                    r == "z" && (r = "x");
                    for (var j = 1, jj = p[length]; j < jj; j++) {
                        r += round(p[j] * zoom) + (j != jj - 1 ? "," : E);
                    }
                    res[push](r);
                }
                return res[join](S);
            };
        
        R[toString] = function () {
            return  "Your browser doesn\u2019t support SVG. Falling down to VML.\nYou are running Rapha\xebl " + this.version;
        };
        thePath = function (pathString, vml) {
            var g = createNode("group");
            g.style.cssText = "position:absolute;left:0;top:0;width:" + vml.width + "px;height:" + vml.height + "px";
            g.coordsize = vml.coordsize;
            g.coordorigin = vml.coordorigin;
            var el = createNode("shape"), ol = el.style;
            ol.width = vml.width + "px";
            ol.height = vml.height + "px";
            el.coordsize = coordsize;
            el.coordorigin = vml.coordorigin;
            g[appendChild](el);
            var p = new Element(el, g, vml),
                attr = {fill: "none", stroke: "#000"};
            pathString && (attr.path = pathString);
            p.type = "path";
            p.path = [];
            p.Path = E;
            setFillAndStroke(p, attr);
            vml.canvas[appendChild](g);
            return p;
        };
        setFillAndStroke = function (o, params) {
            o.attrs = o.attrs || {};
            var node = o.node,
                a = o.attrs,
                s = node.style,
                xy,
                newpath = (params.x != a.x || params.y != a.y || params.width != a.width || params.height != a.height || params.r != a.r) && o.type == "rect",
                res = o;

            for (var par in params) if (params[has](par)) {
                a[par] = params[par];
            }
            if (newpath) {
                a.path = rectPath(a.x, a.y, a.width, a.height, a.r);
                o.X = a.x;
                o.Y = a.y;
                o.W = a.width;
                o.H = a.height;
            }
            params.href && (node.href = params.href);
            params.title && (node.title = params.title);
            params.target && (node.target = params.target);
            params.cursor && (s.cursor = params.cursor);
            "blur" in params && o.blur(params.blur);
            if (params.path && o.type == "path" || newpath) {
                node.path = path2vml(a.path);
            }
            if (params.rotation != null) {
                o.rotate(params.rotation, true);
            }
            if (params.translation) {
                xy = Str(params.translation)[split](separator);
                translate.call(o, xy[0], xy[1]);
                if (o._.rt.cx != null) {
                    o._.rt.cx +=+ xy[0];
                    o._.rt.cy +=+ xy[1];
                    o.setBox(o.attrs, xy[0], xy[1]);
                }
            }
            if (params.scale) {
                xy = Str(params.scale)[split](separator);
                o.scale(+xy[0] || 1, +xy[1] || +xy[0] || 1, +xy[2] || null, +xy[3] || null);
            }
            if ("clip-rect" in params) {
                var rect = Str(params["clip-rect"])[split](separator);
                if (rect[length] == 4) {
                    rect[2] = +rect[2] + (+rect[0]);
                    rect[3] = +rect[3] + (+rect[1]);
                    var div = node.clipRect || doc.createElement("div"),
                        dstyle = div.style,
                        group = node.parentNode;
                    dstyle.clip = R.format("rect({1}px {2}px {3}px {0}px)", rect);
                    if (!node.clipRect) {
                        dstyle.position = "absolute";
                        dstyle.top = 0;
                        dstyle.left = 0;
                        dstyle.width = o.paper.width + "px";
                        dstyle.height = o.paper.height + "px";
                        group.parentNode.insertBefore(div, group);
                        div[appendChild](group);
                        node.clipRect = div;
                    }
                }
                if (!params["clip-rect"]) {
                    node.clipRect && (node.clipRect.style.clip = E);
                }
            }
            if (o.type == "image" && params.src) {
                node.src = params.src;
            }
            if (o.type == "image" && params.opacity) {
                node.filterOpacity = ms + ".Alpha(opacity=" + (params.opacity * 100) + ")";
                s.filter = (node.filterMatrix || E) + (node.filterOpacity || E);
            }
            params.font && (s.font = params.font);
            params["font-family"] && (s.fontFamily = '"' + params["font-family"][split](",")[0][rp](/^['"]+|['"]+$/g, E) + '"');
            params["font-size"] && (s.fontSize = params["font-size"]);
            params["font-weight"] && (s.fontWeight = params["font-weight"]);
            params["font-style"] && (s.fontStyle = params["font-style"]);
            if (params.opacity != null || 
                params["stroke-width"] != null ||
                params.fill != null ||
                params.stroke != null ||
                params["stroke-width"] != null ||
                params["stroke-opacity"] != null ||
                params["fill-opacity"] != null ||
                params["stroke-dasharray"] != null ||
                params["stroke-miterlimit"] != null ||
                params["stroke-linejoin"] != null ||
                params["stroke-linecap"] != null) {
                node = o.shape || node;
                var fill = (node.getElementsByTagName(fillString) && node.getElementsByTagName(fillString)[0]),
                    newfill = false;
                !fill && (newfill = fill = createNode(fillString));
                if ("fill-opacity" in params || "opacity" in params) {
                    var opacity = ((+a["fill-opacity"] + 1 || 2) - 1) * ((+a.opacity + 1 || 2) - 1) * ((+R.getRGB(params.fill).o + 1 || 2) - 1);
                    opacity = mmin(mmax(opacity, 0), 1);
                    fill.opacity = opacity;
                }
                params.fill && (fill.on = true);
                if (fill.on == null || params.fill == "none") {
                    fill.on = false;
                }
                if (fill.on && params.fill) {
                    var isURL = params.fill.match(ISURL);
                    if (isURL) {
                        fill.src = isURL[1];
                        fill.type = "tile";
                    } else {
                        fill.color = R.getRGB(params.fill).hex;
                        fill.src = E;
                        fill.type = "solid";
                        if (R.getRGB(params.fill).error && (res.type in {circle: 1, ellipse: 1} || Str(params.fill).charAt() != "r") && addGradientFill(res, params.fill)) {
                            a.fill = "none";
                            a.gradient = params.fill;
                        }
                    }
                }
                newfill && node[appendChild](fill);
                var stroke = (node.getElementsByTagName("stroke") && node.getElementsByTagName("stroke")[0]),
                newstroke = false;
                !stroke && (newstroke = stroke = createNode("stroke"));
                if ((params.stroke && params.stroke != "none") ||
                    params["stroke-width"] ||
                    params["stroke-opacity"] != null ||
                    params["stroke-dasharray"] ||
                    params["stroke-miterlimit"] ||
                    params["stroke-linejoin"] ||
                    params["stroke-linecap"]) {
                    stroke.on = true;
                }
                (params.stroke == "none" || stroke.on == null || params.stroke == 0 || params["stroke-width"] == 0) && (stroke.on = false);
                var strokeColor = R.getRGB(params.stroke);
                stroke.on && params.stroke && (stroke.color = strokeColor.hex);
                opacity = ((+a["stroke-opacity"] + 1 || 2) - 1) * ((+a.opacity + 1 || 2) - 1) * ((+strokeColor.o + 1 || 2) - 1);
                var width = (toFloat(params["stroke-width"]) || 1) * .75;
                opacity = mmin(mmax(opacity, 0), 1);
                params["stroke-width"] == null && (width = a["stroke-width"]);
                params["stroke-width"] && (stroke.weight = width);
                width && width < 1 && (opacity *= width) && (stroke.weight = 1);
                stroke.opacity = opacity;
                
                params["stroke-linejoin"] && (stroke.joinstyle = params["stroke-linejoin"] || "miter");
                stroke.miterlimit = params["stroke-miterlimit"] || 8;
                params["stroke-linecap"] && (stroke.endcap = params["stroke-linecap"] == "butt" ? "flat" : params["stroke-linecap"] == "square" ? "square" : "round");
                if (params["stroke-dasharray"]) {
                    var dasharray = {
                        "-": "shortdash",
                        ".": "shortdot",
                        "-.": "shortdashdot",
                        "-..": "shortdashdotdot",
                        ". ": "dot",
                        "- ": "dash",
                        "--": "longdash",
                        "- .": "dashdot",
                        "--.": "longdashdot",
                        "--..": "longdashdotdot"
                    };
                    stroke.dashstyle = dasharray[has](params["stroke-dasharray"]) ? dasharray[params["stroke-dasharray"]] : E;
                }
                newstroke && node[appendChild](stroke);
            }
            if (res.type == "text") {
                s = res.paper.span.style;
                a.font && (s.font = a.font);
                a["font-family"] && (s.fontFamily = a["font-family"]);
                a["font-size"] && (s.fontSize = a["font-size"]);
                a["font-weight"] && (s.fontWeight = a["font-weight"]);
                a["font-style"] && (s.fontStyle = a["font-style"]);
                res.node.string && (res.paper.span.innerHTML = Str(res.node.string)[rp](/</g, "&#60;")[rp](/&/g, "&#38;")[rp](/\n/g, "<br>"));
                res.W = a.w = res.paper.span.offsetWidth;
                res.H = a.h = res.paper.span.offsetHeight;
                res.X = a.x;
                res.Y = a.y + round(res.H / 2);
 
                // text-anchor emulationm
                switch (a["text-anchor"]) {
                    case "start":
                        res.node.style["v-text-align"] = "left";
                        res.bbx = round(res.W / 2);
                    break;
                    case "end":
                        res.node.style["v-text-align"] = "right";
                        res.bbx = -round(res.W / 2);
                    break;
                    default:
                        res.node.style["v-text-align"] = "center";
                    break;
                }
            }
        };
        addGradientFill = function (o, gradient) {
            o.attrs = o.attrs || {};
            var attrs = o.attrs,
                fill,
                type = "linear",
                fxfy = ".5 .5";
            o.attrs.gradient = gradient;
            gradient = Str(gradient)[rp](radial_gradient, function (all, fx, fy) {
                type = "radial";
                if (fx && fy) {
                    fx = toFloat(fx);
                    fy = toFloat(fy);
                    pow(fx - .5, 2) + pow(fy - .5, 2) > .25 && (fy = math.sqrt(.25 - pow(fx - .5, 2)) * ((fy > .5) * 2 - 1) + .5);
                    fxfy = fx + S + fy;
                }
                return E;
            });
            gradient = gradient[split](/\s*\-\s*/);
            if (type == "linear") {
                var angle = gradient.shift();
                angle = -toFloat(angle);
                if (isNaN(angle)) {
                    return null;
                }
            }
            var dots = parseDots(gradient);
            if (!dots) {
                return null;
            }
            o = o.shape || o.node;
            fill = o.getElementsByTagName(fillString)[0] || createNode(fillString);
            !fill.parentNode && o.appendChild(fill);
            if (dots[length]) {
                fill.on = true;
                fill.method = "none";
                fill.color = dots[0].color;
                fill.color2 = dots[dots[length] - 1].color;
                var clrs = [];
                for (var i = 0, ii = dots[length]; i < ii; i++) {
                    dots[i].offset && clrs[push](dots[i].offset + S + dots[i].color);
                }
                fill.colors && (fill.colors.value = clrs[length] ? clrs[join]() : "0% " + fill.color);
                if (type == "radial") {
                    fill.type = "gradientradial";
                    fill.focus = "100%";
                    fill.focussize = fxfy;
                    fill.focusposition = fxfy;
                } else {
                    fill.type = "gradient";
                    fill.angle = (270 - angle) % 360;
                }
            }
            return 1;
        };
        Element = function (node, group, vml) {
            var Rotation = 0,
                RotX = 0,
                RotY = 0,
                Scale = 1;
            this[0] = node;
            this.id = R._oid++;
            this.node = node;
            node.raphael = this;
            this.X = 0;
            this.Y = 0;
            this.attrs = {};
            this.Group = group;
            this.paper = vml;
            this._ = {
                tx: 0,
                ty: 0,
                rt: {deg:0},
                sx: 1,
                sy: 1
            };
            !vml.bottom && (vml.bottom = this);
            this.prev = vml.top;
            vml.top && (vml.top.next = this);
            vml.top = this;
            this.next = null;
        };
        elproto = Element[proto];
        elproto.rotate = function (deg, cx, cy) {
            if (this.removed) {
                return this;
            }
            if (deg == null) {
                if (this._.rt.cx) {
                    return [this._.rt.deg, this._.rt.cx, this._.rt.cy][join](S);
                }
                return this._.rt.deg;
            }
            deg = Str(deg)[split](separator);
            if (deg[length] - 1) {
                cx = toFloat(deg[1]);
                cy = toFloat(deg[2]);
            }
            deg = toFloat(deg[0]);
            if (cx != null) {
                this._.rt.deg = deg;
            } else {
                this._.rt.deg += deg;
            }
            cy == null && (cx = null);
            this._.rt.cx = cx;
            this._.rt.cy = cy;
            this.setBox(this.attrs, cx, cy);
            this.Group.style.rotation = this._.rt.deg;
            // gradient fix for rotation. TODO
            // var fill = (this.shape || this.node).getElementsByTagName(fillString);
            // fill = fill[0] || {};
            // var b = ((360 - this._.rt.deg) - 270) % 360;
            // !R.is(fill.angle, "undefined") && (fill.angle = b);
            return this;
        };
        elproto.setBox = function (params, cx, cy) {
            if (this.removed) {
                return this;
            }
            var gs = this.Group.style,
                os = (this.shape && this.shape.style) || this.node.style;
            params = params || {};
            for (var i in params) if (params[has](i)) {
                this.attrs[i] = params[i];
            }
            cx = cx || this._.rt.cx;
            cy = cy || this._.rt.cy;
            var attr = this.attrs,
                x,
                y,
                w,
                h;
            switch (this.type) {
                case "circle":
                    x = attr.cx - attr.r;
                    y = attr.cy - attr.r;
                    w = h = attr.r * 2;
                    break;
                case "ellipse":
                    x = attr.cx - attr.rx;
                    y = attr.cy - attr.ry;
                    w = attr.rx * 2;
                    h = attr.ry * 2;
                    break;
                case "image":
                    x = +attr.x;
                    y = +attr.y;
                    w = attr.width || 0;
                    h = attr.height || 0;
                    break;
                case "text":
                    this.textpath.v = ["m", round(attr.x), ", ", round(attr.y - 2), "l", round(attr.x) + 1, ", ", round(attr.y - 2)][join](E);
                    x = attr.x - round(this.W / 2);
                    y = attr.y - this.H / 2;
                    w = this.W;
                    h = this.H;
                    break;
                case "rect":
                case "path":
                    if (!this.attrs.path) {
                        x = 0;
                        y = 0;
                        w = this.paper.width;
                        h = this.paper.height;
                    } else {
                        var dim = pathDimensions(this.attrs.path);
                        x = dim.x;
                        y = dim.y;
                        w = dim.width;
                        h = dim.height;
                    }
                    break;
                default:
                    x = 0;
                    y = 0;
                    w = this.paper.width;
                    h = this.paper.height;
                    break;
            }
            cx = (cx == null) ? x + w / 2 : cx;
            cy = (cy == null) ? y + h / 2 : cy;
            var left = cx - this.paper.width / 2,
                top = cy - this.paper.height / 2, t;
            gs.left != (t = left + "px") && (gs.left = t);
            gs.top != (t = top + "px") && (gs.top = t);
            this.X = pathlike[has](this.type) ? -left : x;
            this.Y = pathlike[has](this.type) ? -top : y;
            this.W = w;
            this.H = h;
            if (pathlike[has](this.type)) {
                os.left != (t = -left * zoom + "px") && (os.left = t);
                os.top != (t = -top * zoom + "px") && (os.top = t);
            } else if (this.type == "text") {
                os.left != (t = -left + "px") && (os.left = t);
                os.top != (t = -top + "px") && (os.top = t);
            } else {
                gs.width != (t = this.paper.width + "px") && (gs.width = t);
                gs.height != (t = this.paper.height + "px") && (gs.height = t);
                os.left != (t = x - left + "px") && (os.left = t);
                os.top != (t = y - top + "px") && (os.top = t);
                os.width != (t = w + "px") && (os.width = t);
                os.height != (t = h + "px") && (os.height = t);
            }
        };
        elproto.hide = function () {
            !this.removed && (this.Group.style.display = "none");
            return this;
        };
        elproto.show = function () {
            !this.removed && (this.Group.style.display = "block");
            return this;
        };
        elproto.getBBox = function () {
            if (this.removed) {
                return this;
            }
            if (pathlike[has](this.type)) {
                return pathDimensions(this.attrs.path);
            }
            return {
                x: this.X + (this.bbx || 0),
                y: this.Y,
                width: this.W,
                height: this.H
            };
        };
        elproto.remove = function () {
            if (this.removed) {
                return;
            }
            tear(this, this.paper);
            this.node.parentNode.removeChild(this.node);
            this.Group.parentNode.removeChild(this.Group);
            this.shape && this.shape.parentNode.removeChild(this.shape);
            for (var i in this) {
                delete this[i];
            }
            this.removed = true;
        };
        elproto.attr = function (name, value) {
            if (this.removed) {
                return this;
            }
            if (name == null) {
                var res = {};
                for (var i in this.attrs) if (this.attrs[has](i)) {
                    res[i] = this.attrs[i];
                }
                this._.rt.deg && (res.rotation = this.rotate());
                (this._.sx != 1 || this._.sy != 1) && (res.scale = this.scale());
                res.gradient && res.fill == "none" && (res.fill = res.gradient) && delete res.gradient;
                return res;
            }
            if (value == null && R.is(name, "string")) {
                if (name == "translation") {
                    return translate.call(this);
                }
                if (name == "rotation") {
                    return this.rotate();
                }
                if (name == "scale") {
                    return this.scale();
                }
                if (name == fillString && this.attrs.fill == "none" && this.attrs.gradient) {
                    return this.attrs.gradient;
                }
                return this.attrs[name];
            }
            if (this.attrs && value == null && R.is(name, array)) {
                var ii, values = {};
                for (i = 0, ii = name[length]; i < ii; i++) {
                    values[name[i]] = this.attr(name[i]);
                }
                return values;
            }
            var params;
            if (value != null) {
                params = {};
                params[name] = value;
            }
            value == null && R.is(name, "object") && (params = name);
            if (params) {
                for (var key in this.paper.customAttributes) if (this.paper.customAttributes[has](key) && params[has](key) && R.is(this.paper.customAttributes[key], "function")) {
                    var par = this.paper.customAttributes[key].apply(this, [][concat](params[key]));
                    this.attrs[key] = params[key];
                    for (var subkey in par) if (par[has](subkey)) {
                        params[subkey] = par[subkey];
                    }
                }
                if (params.text && this.type == "text") {
                    this.node.string = params.text;
                }
                setFillAndStroke(this, params);
                if (params.gradient && (({circle: 1, ellipse: 1})[has](this.type) || Str(params.gradient).charAt() != "r")) {
                    addGradientFill(this, params.gradient);
                }
                (!pathlike[has](this.type) || this._.rt.deg) && this.setBox(this.attrs);
            }
            return this;
        };
        elproto.toFront = function () {
            !this.removed && this.Group.parentNode[appendChild](this.Group);
            this.paper.top != this && tofront(this, this.paper);
            return this;
        };
        elproto.toBack = function () {
            if (this.removed) {
                return this;
            }
            if (this.Group.parentNode.firstChild != this.Group) {
                this.Group.parentNode.insertBefore(this.Group, this.Group.parentNode.firstChild);
                toback(this, this.paper);
            }
            return this;
        };
        elproto.insertAfter = function (element) {
            if (this.removed) {
                return this;
            }
            if (element.constructor == Set) {
                element = element[element.length - 1];
            }
            if (element.Group.nextSibling) {
                element.Group.parentNode.insertBefore(this.Group, element.Group.nextSibling);
            } else {
                element.Group.parentNode[appendChild](this.Group);
            }
            insertafter(this, element, this.paper);
            return this;
        };
        elproto.insertBefore = function (element) {
            if (this.removed) {
                return this;
            }
            if (element.constructor == Set) {
                element = element[0];
            }
            element.Group.parentNode.insertBefore(this.Group, element.Group);
            insertbefore(this, element, this.paper);
            return this;
        };
        elproto.blur = function (size) {
            var s = this.node.runtimeStyle,
                f = s.filter;
            f = f.replace(blurregexp, E);
            if (+size !== 0) {
                this.attrs.blur = size;
                s.filter = f + S + ms + ".Blur(pixelradius=" + (+size || 1.5) + ")";
                s.margin = R.format("-{0}px 0 0 -{0}px", round(+size || 1.5));
            } else {
                s.filter = f;
                s.margin = 0;
                delete this.attrs.blur;
            }
        };
 
        theCircle = function (vml, x, y, r) {
            var g = createNode("group"),
                o = createNode("oval"),
                ol = o.style;
            g.style.cssText = "position:absolute;left:0;top:0;width:" + vml.width + "px;height:" + vml.height + "px";
            g.coordsize = coordsize;
            g.coordorigin = vml.coordorigin;
            g[appendChild](o);
            var res = new Element(o, g, vml);
            res.type = "circle";
            setFillAndStroke(res, {stroke: "#000", fill: "none"});
            res.attrs.cx = x;
            res.attrs.cy = y;
            res.attrs.r = r;
            res.setBox({x: x - r, y: y - r, width: r * 2, height: r * 2});
            vml.canvas[appendChild](g);
            return res;
        };
        function rectPath(x, y, w, h, r) {
            if (r) {
                return R.format("M{0},{1}l{2},0a{3},{3},0,0,1,{3},{3}l0,{5}a{3},{3},0,0,1,{4},{3}l{6},0a{3},{3},0,0,1,{4},{4}l0,{7}a{3},{3},0,0,1,{3},{4}z", x + r, y, w - r * 2, r, -r, h - r * 2, r * 2 - w, r * 2 - h);
            } else {
                return R.format("M{0},{1}l{2},0,0,{3},{4},0z", x, y, w, h, -w);
            }
        }
        theRect = function (vml, x, y, w, h, r) {
            var path = rectPath(x, y, w, h, r),
                res = vml.path(path),
                a = res.attrs;
            res.X = a.x = x;
            res.Y = a.y = y;
            res.W = a.width = w;
            res.H = a.height = h;
            a.r = r;
            a.path = path;
            res.type = "rect";
            return res;
        };
        theEllipse = function (vml, x, y, rx, ry) {
            var g = createNode("group"),
                o = createNode("oval"),
                ol = o.style;
            g.style.cssText = "position:absolute;left:0;top:0;width:" + vml.width + "px;height:" + vml.height + "px";
            g.coordsize = coordsize;
            g.coordorigin = vml.coordorigin;
            g[appendChild](o);
            var res = new Element(o, g, vml);
            res.type = "ellipse";
            setFillAndStroke(res, {stroke: "#000"});
            res.attrs.cx = x;
            res.attrs.cy = y;
            res.attrs.rx = rx;
            res.attrs.ry = ry;
            res.setBox({x: x - rx, y: y - ry, width: rx * 2, height: ry * 2});
            vml.canvas[appendChild](g);
            return res;
        };
        theImage = function (vml, src, x, y, w, h) {
            var g = createNode("group"),
                o = createNode("image");
            g.style.cssText = "position:absolute;left:0;top:0;width:" + vml.width + "px;height:" + vml.height + "px";
            g.coordsize = coordsize;
            g.coordorigin = vml.coordorigin;
            o.src = src;
            g[appendChild](o);
            var res = new Element(o, g, vml);
            res.type = "image";
            res.attrs.src = src;
            res.attrs.x = x;
            res.attrs.y = y;
            res.attrs.w = w;
            res.attrs.h = h;
            res.setBox({x: x, y: y, width: w, height: h});
            vml.canvas[appendChild](g);
            return res;
        };
        theText = function (vml, x, y, text) {
            var g = createNode("group"),
                el = createNode("shape"),
                ol = el.style,
                path = createNode("path"),
                ps = path.style,
                o = createNode("textpath");
            g.style.cssText = "position:absolute;left:0;top:0;width:" + vml.width + "px;height:" + vml.height + "px";
            g.coordsize = coordsize;
            g.coordorigin = vml.coordorigin;
            path.v = R.format("m{0},{1}l{2},{1}", round(x * 10), round(y * 10), round(x * 10) + 1);
            path.textpathok = true;
            ol.width = vml.width;
            ol.height = vml.height;
            o.string = Str(text);
            o.on = true;
            el[appendChild](o);
            el[appendChild](path);
            g[appendChild](el);
            var res = new Element(o, g, vml);
            res.shape = el;
            res.textpath = path;
            res.type = "text";
            res.attrs.text = text;
            res.attrs.x = x;
            res.attrs.y = y;
            res.attrs.w = 1;
            res.attrs.h = 1;
            setFillAndStroke(res, {font: availableAttrs.font, stroke: "none", fill: "#000"});
            res.setBox();
            vml.canvas[appendChild](g);
            return res;
        };
        setSize = function (width, height) {
            var cs = this.canvas.style;
            width == +width && (width += "px");
            height == +height && (height += "px");
            cs.width = width;
            cs.height = height;
            cs.clip = "rect(0 " + width + " " + height + " 0)";
            return this;
        };
        var createNode;
        doc.createStyleSheet().addRule(".rvml", "behavior:url(#default#VML)");
        try {
            !doc.namespaces.rvml && doc.namespaces.add("rvml", "urn:schemas-microsoft-com:vml");
            createNode = function (tagName) {
                return doc.createElement('<rvml:' + tagName + ' class="rvml">');
            };
        } catch (e) {
            createNode = function (tagName) {
                return doc.createElement('<' + tagName + ' xmlns="urn:schemas-microsoft.com:vml" class="rvml">');
            };
        }
        create = function () {
            var con = getContainer[apply](0, arguments),
                container = con.container,
                height = con.height,
                s,
                width = con.width,
                x = con.x,
                y = con.y;
            if (!container) {
                throw new Error("VML container not found.");
            }
            var res = new Paper,
                c = res.canvas = doc.createElement("div"),
                cs = c.style;
            x = x || 0;
            y = y || 0;
            width = width || 512;
            height = height || 342;
            width == +width && (width += "px");
            height == +height && (height += "px");
            res.width = 1e3;
            res.height = 1e3;
            res.coordsize = zoom * 1e3 + S + zoom * 1e3;
            res.coordorigin = "0 0";
            res.span = doc.createElement("span");
            res.span.style.cssText = "position:absolute;left:-9999em;top:-9999em;padding:0;margin:0;line-height:1;display:inline;";
            c[appendChild](res.span);
            cs.cssText = R.format("top:0;left:0;width:{0};height:{1};display:inline-block;position:relative;clip:rect(0 {0} {1} 0);overflow:hidden", width, height);
            if (container == 1) {
                doc.body[appendChild](c);
                cs.left = x + "px";
                cs.top = y + "px";
                cs.position = "absolute";
            } else {
                if (container.firstChild) {
                    container.insertBefore(c, container.firstChild);
                } else {
                    container[appendChild](c);
                }
            }
            plugins.call(res, res, R.fn);
            return res;
        };
        paperproto.clear = function () {
            this.canvas.innerHTML = E;
            this.span = doc.createElement("span");
            this.span.style.cssText = "position:absolute;left:-9999em;top:-9999em;padding:0;margin:0;line-height:1;display:inline;";
            this.canvas[appendChild](this.span);
            this.bottom = this.top = null;
        };
        paperproto.remove = function () {
            this.canvas.parentNode.removeChild(this.canvas);
            for (var i in this) {
                this[i] = removed(i);
            }
            return true;
        };
    }
 
    // rest
    // WebKit rendering bug workaround method
    var version = navigator.userAgent.match(/Version\/(.*?)\s/);
    if ((navigator.vendor == "Apple Computer, Inc.") && (version && version[1] < 4 || navigator.platform.slice(0, 2) == "iP")) {
        paperproto.safari = function () {
            var rect = this.rect(-99, -99, this.width + 99, this.height + 99).attr({stroke: "none"});
            win.setTimeout(function () {rect.remove();});
        };
    } else {
        paperproto.safari = function () {};
    }
 
    // Events
    var preventDefault = function () {
        this.returnValue = false;
    },
    preventTouch = function () {
        return this.originalEvent.preventDefault();
    },
    stopPropagation = function () {
        this.cancelBubble = true;
    },
    stopTouch = function () {
        return this.originalEvent.stopPropagation();
    },
    addEvent = (function () {
        if (doc.addEventListener) {
            return function (obj, type, fn, element) {
                var realName = supportsTouch && touchMap[type] ? touchMap[type] : type;
                var f = function (e) {
                    if (supportsTouch && touchMap[has](type)) {
                        for (var i = 0, ii = e.targetTouches && e.targetTouches.length; i < ii; i++) {
                            if (e.targetTouches[i].target == obj) {
                                var olde = e;
                                e = e.targetTouches[i];
                                e.originalEvent = olde;
                                e.preventDefault = preventTouch;
                                e.stopPropagation = stopTouch;
                                break;
                            }
                        }
                    }
                    return fn.call(element, e);
                };
                obj.addEventListener(realName, f, false);
                return function () {
                    obj.removeEventListener(realName, f, false);
                    return true;
                };
            };
        } else if (doc.attachEvent) {
            return function (obj, type, fn, element) {
                var f = function (e) {
                    e = e || win.event;
                    e.preventDefault = e.preventDefault || preventDefault;
                    e.stopPropagation = e.stopPropagation || stopPropagation;
                    return fn.call(element, e);
                };
                obj.attachEvent("on" + type, f);
                var detacher = function () {
                    obj.detachEvent("on" + type, f);
                    return true;
                };
                return detacher;
            };
        }
    })(),
    drag = [],
    dragMove = function (e) {
        var x = e.clientX,
            y = e.clientY,
            scrollY = doc.documentElement.scrollTop || doc.body.scrollTop,
            scrollX = doc.documentElement.scrollLeft || doc.body.scrollLeft,
            dragi,
            j = drag.length;
        while (j--) {
            dragi = drag[j];
            if (supportsTouch) {
                var i = e.touches.length,
                    touch;
                while (i--) {
                    touch = e.touches[i];
                    if (touch.identifier == dragi.el._drag.id) {
                        x = touch.clientX;
                        y = touch.clientY;
                        (e.originalEvent ? e.originalEvent : e).preventDefault();
                        break;
                    }
                }
            } else {
                e.preventDefault();
            }
            x += scrollX;
            y += scrollY;
            dragi.move && dragi.move.call(dragi.move_scope || dragi.el, x - dragi.el._drag.x, y - dragi.el._drag.y, x, y, e);
        }
    },
    dragUp = function (e) {
        R.unmousemove(dragMove).unmouseup(dragUp);
        var i = drag.length,
            dragi;
        while (i--) {
            dragi = drag[i];
            dragi.el._drag = {};
            dragi.end && dragi.end.call(dragi.end_scope || dragi.start_scope || dragi.move_scope || dragi.el, e);
        }
        drag = [];
    };
    for (var i = events[length]; i--;) {
        (function (eventName) {
            R[eventName] = Element[proto][eventName] = function (fn, scope) {
                if (R.is(fn, "function")) {
                    this.events = this.events || [];
                    this.events.push({name: eventName, f: fn, unbind: addEvent(this.shape || this.node || doc, eventName, fn, scope || this)});
                }
                return this;
            };
            R["un" + eventName] = Element[proto]["un" + eventName] = function (fn) {
                var events = this.events,
                    l = events[length];
                while (l--) if (events[l].name == eventName && events[l].f == fn) {
                    events[l].unbind();
                    events.splice(l, 1);
                    !events.length && delete this.events;
                    return this;
                }
                return this;
            };
        })(events[i]);
    }
    elproto.hover = function (f_in, f_out, scope_in, scope_out) {
        return this.mouseover(f_in, scope_in).mouseout(f_out, scope_out || scope_in);
    };
    elproto.unhover = function (f_in, f_out) {
        return this.unmouseover(f_in).unmouseout(f_out);
    };
    elproto.drag = function (onmove, onstart, onend, move_scope, start_scope, end_scope) {
        this._drag = {};
        this.mousedown(function (e) {
            (e.originalEvent || e).preventDefault();
            var scrollY = doc.documentElement.scrollTop || doc.body.scrollTop,
                scrollX = doc.documentElement.scrollLeft || doc.body.scrollLeft;
            this._drag.x = e.clientX + scrollX;
            this._drag.y = e.clientY + scrollY;
            this._drag.id = e.identifier;
            onstart && onstart.call(start_scope || move_scope || this, e.clientX + scrollX, e.clientY + scrollY, e);
            !drag.length && R.mousemove(dragMove).mouseup(dragUp);
            drag.push({el: this, move: onmove, end: onend, move_scope: move_scope, start_scope: start_scope, end_scope: end_scope});
        });
        return this;
    };
    elproto.undrag = function (onmove, onstart, onend) {
        var i = drag.length;
        while (i--) {
            drag[i].el == this && (drag[i].move == onmove && drag[i].end == onend) && drag.splice(i++, 1);
        }
        !drag.length && R.unmousemove(dragMove).unmouseup(dragUp);
    };
    paperproto.circle = function (x, y, r) {
        return theCircle(this, x || 0, y || 0, r || 0);
    };
    paperproto.rect = function (x, y, w, h, r) {
        return theRect(this, x || 0, y || 0, w || 0, h || 0, r || 0);
    };
    paperproto.ellipse = function (x, y, rx, ry) {
        return theEllipse(this, x || 0, y || 0, rx || 0, ry || 0);
    };
    paperproto.path = function (pathString) {
        pathString && !R.is(pathString, string) && !R.is(pathString[0], array) && (pathString += E);
        return thePath(R.format[apply](R, arguments), this);
    };
    paperproto.image = function (src, x, y, w, h) {
        return theImage(this, src || "about:blank", x || 0, y || 0, w || 0, h || 0);
    };
    paperproto.text = function (x, y, text) {
        return theText(this, x || 0, y || 0, Str(text));
    };
    paperproto.set = function (itemsArray) {
        arguments[length] > 1 && (itemsArray = Array[proto].splice.call(arguments, 0, arguments[length]));
        return new Set(itemsArray);
    };
    paperproto.setSize = setSize;
    paperproto.top = paperproto.bottom = null;
    paperproto.raphael = R;
    function x_y() {
        return this.x + S + this.y;
    }
    elproto.resetScale = function () {
        if (this.removed) {
            return this;
        }
        this._.sx = 1;
        this._.sy = 1;
        this.attrs.scale = "1 1";
    };
    elproto.scale = function (x, y, cx, cy) {
        if (this.removed) {
            return this;
        }
        if (x == null && y == null) {
            return {
                x: this._.sx,
                y: this._.sy,
                toString: x_y
            };
        }
        y = y || x;
        !+y && (y = x);
        var dx,
            dy,
            dcx,
            dcy,
            a = this.attrs;
        if (x != 0) {
            var bb = this.getBBox(),
                rcx = bb.x + bb.width / 2,
                rcy = bb.y + bb.height / 2,
                kx = abs(x / this._.sx),
                ky = abs(y / this._.sy);
            cx = (+cx || cx == 0) ? cx : rcx;
            cy = (+cy || cy == 0) ? cy : rcy;
            var posx = this._.sx > 0,
                posy = this._.sy > 0,
                dirx = ~~(x / abs(x)),
                diry = ~~(y / abs(y)),
                dkx = kx * dirx,
                dky = ky * diry,
                s = this.node.style,
                ncx = cx + abs(rcx - cx) * dkx * (rcx > cx == posx ? 1 : -1),
                ncy = cy + abs(rcy - cy) * dky * (rcy > cy == posy ? 1 : -1),
                fr = (x * dirx > y * diry ? ky : kx);
            switch (this.type) {
                case "rect":
                case "image":
                    var neww = a.width * kx,
                        newh = a.height * ky;
                    this.attr({
                        height: newh,
                        r: a.r * fr,
                        width: neww,
                        x: ncx - neww / 2,
                        y: ncy - newh / 2
                    });
                    break;
                case "circle":
                case "ellipse":
                    this.attr({
                        rx: a.rx * kx,
                        ry: a.ry * ky,
                        r: a.r * fr,
                        cx: ncx,
                        cy: ncy
                    });
                    break;
                case "text":
                    this.attr({
                        x: ncx,
                        y: ncy
                    });
                    break;
                case "path":
                    var path = pathToRelative(a.path),
                        skip = true,
                        fx = posx ? dkx : kx,
                        fy = posy ? dky : ky;
                    for (var i = 0, ii = path[length]; i < ii; i++) {
                        var p = path[i],
                            P0 = upperCase.call(p[0]);
                        if (P0 == "M" && skip) {
                            continue;
                        } else {
                            skip = false;
                        }
                        if (P0 == "A") {
                            p[path[i][length] - 2] *= fx;
                            p[path[i][length] - 1] *= fy;
                            p[1] *= kx;
                            p[2] *= ky;
                            p[5] = +(dirx + diry ? !!+p[5] : !+p[5]);
                        } else if (P0 == "H") {
                            for (var j = 1, jj = p[length]; j < jj; j++) {
                                p[j] *= fx;
                            }
                        } else if (P0 == "V") {
                            for (j = 1, jj = p[length]; j < jj; j++) {
                                p[j] *= fy;
                            }
                         } else {
                            for (j = 1, jj = p[length]; j < jj; j++) {
                                p[j] *= (j % 2) ? fx : fy;
                            }
                        }
                    }
                    var dim2 = pathDimensions(path);
                    dx = ncx - dim2.x - dim2.width / 2;
                    dy = ncy - dim2.y - dim2.height / 2;
                    path[0][1] += dx;
                    path[0][2] += dy;
                    this.attr({path: path});
                break;
            }
            if (this.type in {text: 1, image:1} && (dirx != 1 || diry != 1)) {
                if (this.transformations) {
                    this.transformations[2] = "scale("[concat](dirx, ",", diry, ")");
                    this.node[setAttribute]("transform", this.transformations[join](S));
                    dx = (dirx == -1) ? -a.x - (neww || 0) : a.x;
                    dy = (diry == -1) ? -a.y - (newh || 0) : a.y;
                    this.attr({x: dx, y: dy});
                    a.fx = dirx - 1;
                    a.fy = diry - 1;
                } else {
                    this.node.filterMatrix = ms + ".Matrix(M11="[concat](dirx,
                        ", M12=0, M21=0, M22=", diry,
                        ", Dx=0, Dy=0, sizingmethod='auto expand', filtertype='bilinear')");
                    s.filter = (this.node.filterMatrix || E) + (this.node.filterOpacity || E);
                }
            } else {
                if (this.transformations) {
                    this.transformations[2] = E;
                    this.node[setAttribute]("transform", this.transformations[join](S));
                    a.fx = 0;
                    a.fy = 0;
                } else {
                    this.node.filterMatrix = E;
                    s.filter = (this.node.filterMatrix || E) + (this.node.filterOpacity || E);
                }
            }
            a.scale = [x, y, cx, cy][join](S);
            this._.sx = x;
            this._.sy = y;
        }
        return this;
    };
    elproto.clone = function () {
        if (this.removed) {
            return null;
        }
        var attr = this.attr();
        delete attr.scale;
        delete attr.translation;
        return this.paper[this.type]().attr(attr);
    };
    var curveslengths = {},
    getPointAtSegmentLength = function (p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, length) {
        var len = 0,
            precision = 100,
            name = [p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y].join(),
            cache = curveslengths[name],
            old, dot;
        !cache && (curveslengths[name] = cache = {data: []});
        cache.timer && clearTimeout(cache.timer);
        cache.timer = setTimeout(function () {delete curveslengths[name];}, 2000);
        if (length != null) {
            var total = getPointAtSegmentLength(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y);
            precision = ~~total * 10;
        }
        for (var i = 0; i < precision + 1; i++) {
            if (cache.data[length] > i) {
                dot = cache.data[i * precision];
            } else {
                dot = R.findDotsAtSegment(p1x, p1y, c1x, c1y, c2x, c2y, p2x, p2y, i / precision);
                cache.data[i] = dot;
            }
            i && (len += pow(pow(old.x - dot.x, 2) + pow(old.y - dot.y, 2), .5));
            if (length != null && len >= length) {
                return dot;
            }
            old = dot;
        }
        if (length == null) {
            return len;
        }
    },
    getLengthFactory = function (istotal, subpath) {
        return function (path, length, onlystart) {
            path = path2curve(path);
            var x, y, p, l, sp = "", subpaths = {}, point,
                len = 0;
            for (var i = 0, ii = path.length; i < ii; i++) {
                p = path[i];
                if (p[0] == "M") {
                    x = +p[1];
                    y = +p[2];
                } else {
                    l = getPointAtSegmentLength(x, y, p[1], p[2], p[3], p[4], p[5], p[6]);
                    if (len + l > length) {
                        if (subpath && !subpaths.start) {
                            point = getPointAtSegmentLength(x, y, p[1], p[2], p[3], p[4], p[5], p[6], length - len);
                            sp += ["C", point.start.x, point.start.y, point.m.x, point.m.y, point.x, point.y];
                            if (onlystart) {return sp;}
                            subpaths.start = sp;
                            sp = ["M", point.x, point.y + "C", point.n.x, point.n.y, point.end.x, point.end.y, p[5], p[6]][join]();
                            len += l;
                            x = +p[5];
                            y = +p[6];
                            continue;
                        }
                        if (!istotal && !subpath) {
                            point = getPointAtSegmentLength(x, y, p[1], p[2], p[3], p[4], p[5], p[6], length - len);
                            return {x: point.x, y: point.y, alpha: point.alpha};
                        }
                    }
                    len += l;
                    x = +p[5];
                    y = +p[6];
                }
                sp += p;
            }
            subpaths.end = sp;
            point = istotal ? len : subpath ? subpaths : R.findDotsAtSegment(x, y, p[1], p[2], p[3], p[4], p[5], p[6], 1);
            point.alpha && (point = {x: point.x, y: point.y, alpha: point.alpha});
            return point;
        };
    };
    var getTotalLength = getLengthFactory(1),
        getPointAtLength = getLengthFactory(),
        getSubpathsAtLength = getLengthFactory(0, 1);
    elproto.getTotalLength = function () {
        if (this.type != "path") {return;}
        if (this.node.getTotalLength) {
            return this.node.getTotalLength();
        }
        return getTotalLength(this.attrs.path);
    };
    elproto.getPointAtLength = function (length) {
        if (this.type != "path") {return;}
        return getPointAtLength(this.attrs.path, length);
    };
    elproto.getSubpath = function (from, to) {
        if (this.type != "path") {return;}
        if (abs(this.getTotalLength() - to) < "1e-6") {
            return getSubpathsAtLength(this.attrs.path, from).end;
        }
        var a = getSubpathsAtLength(this.attrs.path, to, 1);
        return from ? getSubpathsAtLength(a, from).end : a;
    };

    // animation easing formulas
    R.easing_formulas = {
        linear: function (n) {
            return n;
        },
        "<": function (n) {
            return pow(n, 3);
        },
        ">": function (n) {
            return pow(n - 1, 3) + 1;
        },
        "<>": function (n) {
            n = n * 2;
            if (n < 1) {
                return pow(n, 3) / 2;
            }
            n -= 2;
            return (pow(n, 3) + 2) / 2;
        },
        backIn: function (n) {
            var s = 1.70158;
            return n * n * ((s + 1) * n - s);
        },
        backOut: function (n) {
            n = n - 1;
            var s = 1.70158;
            return n * n * ((s + 1) * n + s) + 1;
        },
        elastic: function (n) {
            if (n == 0 || n == 1) {
                return n;
            }
            var p = .3,
                s = p / 4;
            return pow(2, -10 * n) * math.sin((n - s) * (2 * PI) / p) + 1;
        },
        bounce: function (n) {
            var s = 7.5625,
                p = 2.75,
                l;
            if (n < (1 / p)) {
                l = s * n * n;
            } else {
                if (n < (2 / p)) {
                    n -= (1.5 / p);
                    l = s * n * n + .75;
                } else {
                    if (n < (2.5 / p)) {
                        n -= (2.25 / p);
                        l = s * n * n + .9375;
                    } else {
                        n -= (2.625 / p);
                        l = s * n * n + .984375;
                    }
                }
            }
            return l;
        }
    };

    var animationElements = [],
        animation = function () {
            var Now = +new Date;
            for (var l = 0; l < animationElements[length]; l++) {
                var e = animationElements[l];
                if (e.stop || e.el.removed) {
                    continue;
                }
                var time = Now - e.start,
                    ms = e.ms,
                    easing = e.easing,
                    from = e.from,
                    diff = e.diff,
                    to = e.to,
                    t = e.t,
                    that = e.el,
                    set = {},
                    now;
                if (time < ms) {
                    var pos = easing(time / ms);
                    for (var attr in from) if (from[has](attr)) {
                        switch (availableAnimAttrs[attr]) {
                            case "along":
                                now = pos * ms * diff[attr];
                                to.back && (now = to.len - now);
                                var point = getPointAtLength(to[attr], now);
                                that.translate(diff.sx - diff.x || 0, diff.sy - diff.y || 0);
                                diff.x = point.x;
                                diff.y = point.y;
                                that.translate(point.x - diff.sx, point.y - diff.sy);
                                to.rot && that.rotate(diff.r + point.alpha, point.x, point.y);
                                break;
                            case nu:
                                now = +from[attr] + pos * ms * diff[attr];
                                break;
                            case "colour":
                                now = "rgb(" + [
                                    upto255(round(from[attr].r + pos * ms * diff[attr].r)),
                                    upto255(round(from[attr].g + pos * ms * diff[attr].g)),
                                    upto255(round(from[attr].b + pos * ms * diff[attr].b))
                                ][join](",") + ")";
                                break;
                            case "path":
                                now = [];
                                for (var i = 0, ii = from[attr][length]; i < ii; i++) {
                                    now[i] = [from[attr][i][0]];
                                    for (var j = 1, jj = from[attr][i][length]; j < jj; j++) {
                                        now[i][j] = +from[attr][i][j] + pos * ms * diff[attr][i][j];
                                    }
                                    now[i] = now[i][join](S);
                                }
                                now = now[join](S);
                                break;
                            case "csv":
                                switch (attr) {
                                    case "translation":
                                        var x = pos * ms * diff[attr][0] - t.x,
                                            y = pos * ms * diff[attr][1] - t.y;
                                        t.x += x;
                                        t.y += y;
                                        now = x + S + y;
                                    break;
                                    case "rotation":
                                        now = +from[attr][0] + pos * ms * diff[attr][0];
                                        from[attr][1] && (now += "," + from[attr][1] + "," + from[attr][2]);
                                    break;
                                    case "scale":
                                        now = [+from[attr][0] + pos * ms * diff[attr][0], +from[attr][1] + pos * ms * diff[attr][1], (2 in to[attr] ? to[attr][2] : E), (3 in to[attr] ? to[attr][3] : E)][join](S);
                                    break;
                                    case "clip-rect":
                                        now = [];
                                        i = 4;
                                        while (i--) {
                                            now[i] = +from[attr][i] + pos * ms * diff[attr][i];
                                        }
                                    break;
                                }
                                break;
                            default:
                              var from2 = [].concat(from[attr]);
                                now = [];
                                i = that.paper.customAttributes[attr].length;
                                while (i--) {
                                    now[i] = +from2[i] + pos * ms * diff[attr][i];
                                }
                                break;
                        }
                        set[attr] = now;
                    }
                    that.attr(set);
                    that._run && that._run.call(that);
                } else {
                    if (to.along) {
                        point = getPointAtLength(to.along, to.len * !to.back);
                        that.translate(diff.sx - (diff.x || 0) + point.x - diff.sx, diff.sy - (diff.y || 0) + point.y - diff.sy);
                        to.rot && that.rotate(diff.r + point.alpha, point.x, point.y);
                    }
                    (t.x || t.y) && that.translate(-t.x, -t.y);
                    to.scale && (to.scale += E);
                    that.attr(to);
                    animationElements.splice(l--, 1);
                }
            }
            R.svg && that && that.paper && that.paper.safari();
            animationElements[length] && setTimeout(animation);
        },
        keyframesRun = function (attr, element, time, prev, prevcallback) {
            var dif = time - prev;
            element.timeouts.push(setTimeout(function () {
                R.is(prevcallback, "function") && prevcallback.call(element);
                element.animate(attr, dif, attr.easing);
            }, prev));
        },
        upto255 = function (color) {
            return mmax(mmin(color, 255), 0);
        },
        translate = function (x, y) {
            if (x == null) {
                return {x: this._.tx, y: this._.ty, toString: x_y};
            }
            this._.tx += +x;
            this._.ty += +y;
            switch (this.type) {
                case "circle":
                case "ellipse":
                    this.attr({cx: +x + this.attrs.cx, cy: +y + this.attrs.cy});
                    break;
                case "rect":
                case "image":
                case "text":
                    this.attr({x: +x + this.attrs.x, y: +y + this.attrs.y});
                    break;
                case "path":
                    var path = pathToRelative(this.attrs.path);
                    path[0][1] += +x;
                    path[0][2] += +y;
                    this.attr({path: path});
                break;
            }
            return this;
        };
    elproto.animateWith = function (element, params, ms, easing, callback) {
        for (var i = 0, ii = animationElements.length; i < ii; i++) {
            if (animationElements[i].el.id == element.id) {
                params.start = animationElements[i].start;
            }
        }
        return this.animate(params, ms, easing, callback);
    };
    elproto.animateAlong = along();
    elproto.animateAlongBack = along(1);
    function along(isBack) {
        return function (path, ms, rotate, callback) {
            var params = {back: isBack};
            R.is(rotate, "function") ? (callback = rotate) : (params.rot = rotate);
            path && path.constructor == Element && (path = path.attrs.path);
            path && (params.along = path);
            return this.animate(params, ms, callback);
        };
    }
    function CubicBezierAtTime(t, p1x, p1y, p2x, p2y, duration) {
        var cx = 3 * p1x,
            bx = 3 * (p2x - p1x) - cx,
            ax = 1 - cx - bx,
            cy = 3 * p1y,
            by = 3 * (p2y - p1y) - cy,
            ay = 1 - cy - by;
        function sampleCurveX(t) {
            return ((ax * t + bx) * t + cx) * t;
        }
        function solve(x, epsilon) {
            var t = solveCurveX(x, epsilon);
            return ((ay * t + by) * t + cy) * t;
        }
        function solveCurveX(x, epsilon) {
            var t0, t1, t2, x2, d2, i;
            for(t2 = x, i = 0; i < 8; i++) {
                x2 = sampleCurveX(t2) - x;
                if (abs(x2) < epsilon) {
                    return t2;
                }
                d2 = (3 * ax * t2 + 2 * bx) * t2 + cx;
                if (abs(d2) < 1e-6) {
                    break;
                }
                t2 = t2 - x2 / d2;
            }
            t0 = 0;
            t1 = 1;
            t2 = x;
            if (t2 < t0) {
                return t0;
            }
            if (t2 > t1) {
                return t1;
            }
            while (t0 < t1) {
                x2 = sampleCurveX(t2);
                if (abs(x2 - x) < epsilon) {
                    return t2;
                }
                if (x > x2) {
                    t0 = t2;
                } else {
                    t1 = t2;
                }
                t2 = (t1 - t0) / 2 + t0;
            }
            return t2;
        }
        return solve(t, 1 / (200 * duration));
    }
    elproto.onAnimation = function (f) {
        this._run = f || 0;
        return this;
    };
    elproto.animate = function (params, ms, easing, callback) {
        var element = this;
        element.timeouts = element.timeouts || [];
        if (R.is(easing, "function") || !easing) {
            callback = easing || null;
        }
        if (element.removed) {
            callback && callback.call(element);
            return element;
        }
        var from = {},
            to = {},
            animateable = false,
            diff = {};
        for (var attr in params) if (params[has](attr)) {
            if (availableAnimAttrs[has](attr) || element.paper.customAttributes[has](attr)) {
                animateable = true;
                from[attr] = element.attr(attr);
                (from[attr] == null) && (from[attr] = availableAttrs[attr]);
                to[attr] = params[attr];
                switch (availableAnimAttrs[attr]) {
                    case "along":
                        var len = getTotalLength(params[attr]);
                        var point = getPointAtLength(params[attr], len * !!params.back);
                        var bb = element.getBBox();
                        diff[attr] = len / ms;
                        diff.tx = bb.x;
                        diff.ty = bb.y;
                        diff.sx = point.x;
                        diff.sy = point.y;
                        to.rot = params.rot;
                        to.back = params.back;
                        to.len = len;
                        params.rot && (diff.r = toFloat(element.rotate()) || 0);
                        break;
                    case nu:
                        diff[attr] = (to[attr] - from[attr]) / ms;
                        break;
                    case "colour":
                        from[attr] = R.getRGB(from[attr]);
                        var toColour = R.getRGB(to[attr]);
                        diff[attr] = {
                            r: (toColour.r - from[attr].r) / ms,
                            g: (toColour.g - from[attr].g) / ms,
                            b: (toColour.b - from[attr].b) / ms
                        };
                        break;
                    case "path":
                        var pathes = path2curve(from[attr], to[attr]);
                        from[attr] = pathes[0];
                        var toPath = pathes[1];
                        diff[attr] = [];
                        for (var i = 0, ii = from[attr][length]; i < ii; i++) {
                            diff[attr][i] = [0];
                            for (var j = 1, jj = from[attr][i][length]; j < jj; j++) {
                                diff[attr][i][j] = (toPath[i][j] - from[attr][i][j]) / ms;
                            }
                        }
                        break;
                    case "csv":
                        var values = Str(params[attr])[split](separator),
                            from2 = Str(from[attr])[split](separator);
                        switch (attr) {
                            case "translation":
                                from[attr] = [0, 0];
                                diff[attr] = [values[0] / ms, values[1] / ms];
                            break;
                            case "rotation":
                                from[attr] = (from2[1] == values[1] && from2[2] == values[2]) ? from2 : [0, values[1], values[2]];
                                diff[attr] = [(values[0] - from[attr][0]) / ms, 0, 0];
                            break;
                            case "scale":
                                params[attr] = values;
                                from[attr] = Str(from[attr])[split](separator);
                                diff[attr] = [(values[0] - from[attr][0]) / ms, (values[1] - from[attr][1]) / ms, 0, 0];
                            break;
                            case "clip-rect":
                                from[attr] = Str(from[attr])[split](separator);
                                diff[attr] = [];
                                i = 4;
                                while (i--) {
                                    diff[attr][i] = (values[i] - from[attr][i]) / ms;
                                }
                            break;
                        }
                        to[attr] = values;
                        break;
                    default:
                        values = [].concat(params[attr]);
                        from2 = [].concat(from[attr]);
                        diff[attr] = [];
                        i = element.paper.customAttributes[attr][length];
                        while (i--) {
                            diff[attr][i] = ((values[i] || 0) - (from2[i] || 0)) / ms;
                        }
                        break;
                }
            }
        }
        if (!animateable) {
            var attrs = [],
                lastcall;
            for (var key in params) if (params[has](key) && animKeyFrames.test(key)) {
                attr = {value: params[key]};
                key == "from" && (key = 0);
                key == "to" && (key = 100);
                attr.key = toInt(key, 10);
                attrs.push(attr);
            }
            attrs.sort(sortByKey);
            if (attrs[0].key) {
                attrs.unshift({key: 0, value: element.attrs});
            }
            for (i = 0, ii = attrs[length]; i < ii; i++) {
                keyframesRun(attrs[i].value, element, ms / 100 * attrs[i].key, ms / 100 * (attrs[i - 1] && attrs[i - 1].key || 0), attrs[i - 1] && attrs[i - 1].value.callback);
            }
            lastcall = attrs[attrs[length] - 1].value.callback;
            if (lastcall) {
                element.timeouts.push(setTimeout(function () {lastcall.call(element);}, ms));
            }
        } else {
            var easyeasy = R.easing_formulas[easing];
            if (!easyeasy) {
                easyeasy = Str(easing).match(bezierrg);
                if (easyeasy && easyeasy[length] == 5) {
                    var curve = easyeasy;
                    easyeasy = function (t) {
                        return CubicBezierAtTime(t, +curve[1], +curve[2], +curve[3], +curve[4], ms);
                    };
                } else {
                    easyeasy = function (t) {
                        return t;
                    };
                }
            }
            animationElements.push({
                start: params.start || +new Date,
                ms: ms,
                easing: easyeasy,
                from: from,
                diff: diff,
                to: to,
                el: element,
                t: {x: 0, y: 0}
            });
            R.is(callback, "function") && (element._ac = setTimeout(function () {
                callback.call(element);
            }, ms));
            animationElements[length] == 1 && setTimeout(animation);
        }
        return this;
    };
    elproto.stop = function () {
        for (var i = 0; i < animationElements.length; i++) {
            animationElements[i].el.id == this.id && animationElements.splice(i--, 1);
        }
        for (i = 0, ii = this.timeouts && this.timeouts.length; i < ii; i++) {
            clearTimeout(this.timeouts[i]);
        }
        this.timeouts = [];
        clearTimeout(this._ac);
        delete this._ac;
        return this;
    };
    elproto.translate = function (x, y) {
        return this.attr({translation: x + " " + y});
    };
    elproto[toString] = function () {
        return "Rapha\xebl\u2019s object";
    };
    R.ae = animationElements;
 
    // Set
    var Set = function (items) {
        this.items = [];
        this[length] = 0;
        this.type = "set";
        if (items) {
            for (var i = 0, ii = items[length]; i < ii; i++) {
                if (items[i] && (items[i].constructor == Element || items[i].constructor == Set)) {
                    this[this.items[length]] = this.items[this.items[length]] = items[i];
                    this[length]++;
                }
            }
        }
    };
    Set[proto][push] = function () {
        var item,
            len;
        for (var i = 0, ii = arguments[length]; i < ii; i++) {
            item = arguments[i];
            if (item && (item.constructor == Element || item.constructor == Set)) {
                len = this.items[length];
                this[len] = this.items[len] = item;
                this[length]++;
            }
        }
        return this;
    };
    Set[proto].pop = function () {
        delete this[this[length]--];
        return this.items.pop();
    };
    for (var method in elproto) if (elproto[has](method)) {
        Set[proto][method] = (function (methodname) {
            return function () {
                for (var i = 0, ii = this.items[length]; i < ii; i++) {
                    this.items[i][methodname][apply](this.items[i], arguments);
                }
                return this;
            };
        })(method);
    }
    Set[proto].attr = function (name, value) {
        if (name && R.is(name, array) && R.is(name[0], "object")) {
            for (var j = 0, jj = name[length]; j < jj; j++) {
                this.items[j].attr(name[j]);
            }
        } else {
            for (var i = 0, ii = this.items[length]; i < ii; i++) {
                this.items[i].attr(name, value);
            }
        }
        return this;
    };
    Set[proto].animate = function (params, ms, easing, callback) {
        (R.is(easing, "function") || !easing) && (callback = easing || null);
        var len = this.items[length],
            i = len,
            item,
            set = this,
            collector;
        callback && (collector = function () {
            !--len && callback.call(set);
        });
        easing = R.is(easing, string) ? easing : collector;
        item = this.items[--i].animate(params, ms, easing, collector);
        while (i--) {
            this.items[i] && !this.items[i].removed && this.items[i].animateWith(item, params, ms, easing, collector);
        }
        return this;
    };
    Set[proto].insertAfter = function (el) {
        var i = this.items[length];
        while (i--) {
            this.items[i].insertAfter(el);
        }
        return this;
    };
    Set[proto].getBBox = function () {
        var x = [],
            y = [],
            w = [],
            h = [];
        for (var i = this.items[length]; i--;) {
            var box = this.items[i].getBBox();
            x[push](box.x);
            y[push](box.y);
            w[push](box.x + box.width);
            h[push](box.y + box.height);
        }
        x = mmin[apply](0, x);
        y = mmin[apply](0, y);
        return {
            x: x,
            y: y,
            width: mmax[apply](0, w) - x,
            height: mmax[apply](0, h) - y
        };
    };
    Set[proto].clone = function (s) {
        s = new Set;
        for (var i = 0, ii = this.items[length]; i < ii; i++) {
            s[push](this.items[i].clone());
        }
        return s;
    };

    R.registerFont = function (font) {
        if (!font.face) {
            return font;
        }
        this.fonts = this.fonts || {};
        var fontcopy = {
                w: font.w,
                face: {},
                glyphs: {}
            },
            family = font.face["font-family"];
        for (var prop in font.face) if (font.face[has](prop)) {
            fontcopy.face[prop] = font.face[prop];
        }
        if (this.fonts[family]) {
            this.fonts[family][push](fontcopy);
        } else {
            this.fonts[family] = [fontcopy];
        }
        if (!font.svg) {
            fontcopy.face["units-per-em"] = toInt(font.face["units-per-em"], 10);
            for (var glyph in font.glyphs) if (font.glyphs[has](glyph)) {
                var path = font.glyphs[glyph];
                fontcopy.glyphs[glyph] = {
                    w: path.w,
                    k: {},
                    d: path.d && "M" + path.d[rp](/[mlcxtrv]/g, function (command) {
                            return {l: "L", c: "C", x: "z", t: "m", r: "l", v: "c"}[command] || "M";
                        }) + "z"
                };
                if (path.k) {
                    for (var k in path.k) if (path[has](k)) {
                        fontcopy.glyphs[glyph].k[k] = path.k[k];
                    }
                }
            }
        }
        return font;
    };
    paperproto.getFont = function (family, weight, style, stretch) {
        stretch = stretch || "normal";
        style = style || "normal";
        weight = +weight || {normal: 400, bold: 700, lighter: 300, bolder: 800}[weight] || 400;
        if (!R.fonts) {
            return;
        }
        var font = R.fonts[family];
        if (!font) {
            var name = new RegExp("(^|\\s)" + family[rp](/[^\w\d\s+!~.:_-]/g, E) + "(\\s|$)", "i");
            for (var fontName in R.fonts) if (R.fonts[has](fontName)) {
                if (name.test(fontName)) {
                    font = R.fonts[fontName];
                    break;
                }
            }
        }
        var thefont;
        if (font) {
            for (var i = 0, ii = font[length]; i < ii; i++) {
                thefont = font[i];
                if (thefont.face["font-weight"] == weight && (thefont.face["font-style"] == style || !thefont.face["font-style"]) && thefont.face["font-stretch"] == stretch) {
                    break;
                }
            }
        }
        return thefont;
    };
    paperproto.print = function (x, y, string, font, size, origin, letter_spacing) {
        origin = origin || "middle"; // baseline|middle
        letter_spacing = mmax(mmin(letter_spacing || 0, 1), -1);
        var out = this.set(),
            letters = Str(string)[split](E),
            shift = 0,
            path = E,
            scale;
        R.is(font, string) && (font = this.getFont(font));
        if (font) {
            scale = (size || 16) / font.face["units-per-em"];
            var bb = font.face.bbox.split(separator),
                top = +bb[0],
                height = +bb[1] + (origin == "baseline" ? bb[3] - bb[1] + (+font.face.descent) : (bb[3] - bb[1]) / 2);
            for (var i = 0, ii = letters[length]; i < ii; i++) {
                var prev = i && font.glyphs[letters[i - 1]] || {},
                    curr = font.glyphs[letters[i]];
                shift += i ? (prev.w || font.w) + (prev.k && prev.k[letters[i]] || 0) + (font.w * letter_spacing) : 0;
                curr && curr.d && out[push](this.path(curr.d).attr({fill: "#000", stroke: "none", translation: [shift, 0]}));
            }
            out.scale(scale, scale, top, height).translate(x - top, y - height);
        }
        return out;
    };

    R.format = function (token, params) {
        var args = R.is(params, array) ? [0][concat](params) : arguments;
        token && R.is(token, string) && args[length] - 1 && (token = token[rp](formatrg, function (str, i) {
            return args[++i] == null ? E : args[i];
        }));
        return token || E;
    };
    R.ninja = function () {
        oldRaphael.was ? (win.Raphael = oldRaphael.is) : delete Raphael;
        return R;
    };
    R.el = elproto;
    R.st = Set[proto];

    oldRaphael.was ? (win.Raphael = R) : (Raphael = R);
})();
/**
*
*  Base64 encode / decode
*  http://www.webtoolkit.info/
*
**/

var Base64 = {

	// private property
	_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

	// public method for encoding
	encode : function (input) {
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;

		input = Base64._utf8_encode(input);

		while (i < input.length) {

			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);

			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;

			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}

			output = output +
			this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
			this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

		}

		return output;
	},

	// public method for decoding
	decode : function (input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;

		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

		while (i < input.length) {

			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));

			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;

			output = output + String.fromCharCode(chr1);

			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}

		}

		output = Base64._utf8_decode(output);

		return output;

	},

	// private method for UTF-8 encoding
	_utf8_encode : function (string) {
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";

		for (var n = 0; n < string.length; n++) {

			var c = string.charCodeAt(n);

			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}

		}

		return utftext;
	},

	// private method for UTF-8 decoding
	_utf8_decode : function (utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while ( i < utftext.length ) {

			c = utftext.charCodeAt(i);

			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			}
			else if((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i+1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else {
				c2 = utftext.charCodeAt(i+1);
				c3 = utftext.charCodeAt(i+2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}

		}

		return string;
	}

}
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.util)
    util = {};

var EventMap =
{
    mousemove: 'mousemove',
    mousedown: 'mousedown',
    mouseup  : 'mouseup'
};

Array.prototype.swap = function (i1, i2)
{
	var tmp = this[i1];
	this[i1] = this[i2];
	this[i2] = tmp;
};

// "each" function for an array
util.each = function (array, func, context) {
    for (var i = 0; i < array.length; ++i)
        func.call(context, array[i], i)
};

util.array = function (arrayLike) {
    var a = [], i = arrayLike.length;
    while (--i >= 0)
        a[i] = arrayLike[i];
    return a;
};

util.isEmpty = function (obj) {
    for (var v in obj)
        return false;
    return true;
};

util.stopEventPropagation = function (event) {
    if ('stopPropagation' in event) // Mozilla, Opera, Safari
        event.stopPropagation();
    else if ('cancelBubble' in event) // IE
        event.cancelBubble = true;
    else
        throw Error("Browser unrecognized");
};

util.preventDefault = function (event) {
    if ('preventDefault' in event)
        event.preventDefault();
    if (Prototype.Browser.IE)
    {
        event.returnValue = false;
        event.keyCode = 0;
    }
    return false;
};

util.setElementTextContent = function (element, text)
{
    if ('textContent' in element) // Mozilla, Opera, Safari
        element.textContent = text;
    else if ('innerText' in element) // IE and others (except Mozilla)
        element.innerText = text;
    else
        throw Error("Browser unrecognized");
};

util.getElementTextContent = function (element)
{
    if ('textContent' in element) // Mozilla, Opera, Safari
        return element.textContent;
    else if ('innerText' in element) // IE and others (except Mozilla)
        return element.innerText;
    else
        throw Error("Browser unrecognized");
};

util.stringPadded = function (string, width, leftAligned) {
	string += '';
	var space = '';
	while (string.length + space.length < width)
		space += ' ';
	if (leftAligned)
		return string + space;
	else
		return space + string;
};

util.idList = function (object) {
	var list = [];
	for (var aid in object) {
		list.push(aid);
	}
	return list;
};

util.mapArray = function (src, map) {
	var dst = [];
	for (var i = 0; i < src.length; ++i) {
		dst.push(map[src[i]]);
	}
	return dst;
};

util.apply = function (array, func) {
	for (var i = 0; i < array.length; ++i)
		array[i] = func(array[i]);
};

util.ifDef = function (dst, src, prop, def)
{
	dst[prop] = !Object.isUndefined(src[prop]) ? src[prop] : def;
};

util.ifDefList = function (dst, src, prop, def)
{
	dst[prop] = !Object.isUndefined(src[prop]) && src[prop] != null ? util.array(src[prop]) : def;
};

util.identityMap = function (array) {
	var map = {};
	for (var i = 0; i < array.length; ++i)
		map[array[i]] = array[i];
	return map;
};

util.stripRight = function (src) {
	var i;
	for (i = 0; i < src.length; ++i)
		if (src[src.lenght - i - 1] != ' ')
			break;
	return src.slice(0, src.length - i);
};

util.stripQuotes = function (str) {
       if (str[0] == '"' && str[str.length - 1] == '"')
               return str.substr(1,str.length-2);
       return str;
}

util.paddedFloat = function (number, width, precision)
{
	var numStr = number.toFixed(precision).replace(',', '.');
	if (numStr.length > width)
		throw new Error("number does not fit");
	return util.stringPadded(numStr, width);
};

util.paddedInt = function (number, width)
{
	var numStr = number.toFixed(0);
	if (numStr.length > width) {
		throw new Error("number does not fit");
	}
	return util.stringPadded(numStr, width);
};

util.arrayAddIfMissing = function (array, item)
{
	for (var i = 0; i < array.length; ++i)
		if (array[i] == item)
			return false;
	array.push(item);
	return true;
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

// 2d-vector constructor and utilities
if (!window.util)
    util = {};

//util.assertDefined = function(v) { if (typeof(v) == 'undefined' || v == null) debugger; }
util.assertDefined = function() { };

util.Vec2 = function (x, y)
{
	if (arguments.length == 0) {
		this.x = 0;
		this.y = 0;
	} else if (arguments.length == 1) {
		this.x = parseFloat(x.x);
		this.y = parseFloat(x.y);
	} else if (arguments.length == 2) {
		this.x = parseFloat(x);
		this.y = parseFloat(y);
	} else {
		throw "util.Vec2(): invalid arguments";
	}
};

util.Vec2.ZERO = new util.Vec2(0, 0);
util.Vec2.UNIT = new util.Vec2(1, 1);

util.Vec2.prototype.length = function ()
{
    return Math.sqrt(this.x * this.x + this.y * this.y);
};

util.Vec2.prototype.equals = function (v)
{
	util.assertDefined(v);
    return this.x == v.x && this.y == v.y;
};

util.Vec2.prototype.add = function (v)
{
	util.assertDefined(v);
    return new util.Vec2(this.x + v.x, this.y + v.y);
};

util.Vec2.prototype.add_ = function (v)
{
	util.assertDefined(v);
    this.x += v.x;
    this.y += v.y;
};

util.Vec2.prototype.sub = function (v)
{
	util.assertDefined(v);
    return new util.Vec2(this.x - v.x, this.y - v.y);
};

util.Vec2.prototype.scaled = function (s)
{
	util.assertDefined(s);
    return new util.Vec2(this.x * s, this.y * s);
};

util.Vec2.prototype.negated = function ()
{
    return new util.Vec2(-this.x, -this.y);
};

util.Vec2.prototype.yComplement = function (y1)
{
    y1 = y1 || 0;
    return new util.Vec2(this.x, y1 - this.y);
};

util.Vec2.prototype.addScaled = function (v, f)
{
	util.assertDefined(v);
	util.assertDefined(f);
    return new util.Vec2(this.x + v.x * f, this.y + v.y * f);
};

util.Vec2.prototype.normalized = function ()
{
    return this.scaled(1 / this.length());
};

util.Vec2.prototype.normalize = function ()
{
    var l = this.length();

    if (l < 0.000001)
        return false;

    this.x /= l;
    this.y /= l;

    return true;
};

util.Vec2.prototype.turnLeft = function ()
{
    return new util.Vec2(-this.y, this.x);
};

util.Vec2.prototype.toString = function ()
{
    return "(" + this.x.toFixed(2) + "," + this.y.toFixed(2) + ")";
};

util.Vec2.dist = function (a, b)
{
	util.assertDefined(a);
	util.assertDefined(b);
    return util.Vec2.diff(a, b).length();
};

util.Vec2.max = function (v1, v2)
{
	util.assertDefined(v1);
	util.assertDefined(v2);
    return new util.Vec2(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y));
};

util.Vec2.min = function (v1, v2)
{
	util.assertDefined(v1);
	util.assertDefined(v2);
    return new util.Vec2(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y));
};

util.Vec2.prototype.max = function (v)
{
	util.assertDefined(v);
    return new util.Vec2.max(this, v);
};

util.Vec2.prototype.min = function (v)
{
	util.assertDefined(v);
    return new util.Vec2.min(this, v);
};

util.Vec2.prototype.ceil = function ()
{
    return new util.Vec2(Math.ceil(this.x), Math.ceil(this.y));
};

util.Vec2.prototype.floor = function ()
{
    return new util.Vec2(Math.floor(this.x), Math.floor(this.y));
};

util.Vec2.sum = function (v1, v2)
{
	util.assertDefined(v1);
	util.assertDefined(v2);
    return new util.Vec2(v1.x + v2.x, v1.y + v2.y);
};

util.Vec2.dot = function (v1, v2)
{
	util.assertDefined(v1);
	util.assertDefined(v2);
    return v1.x * v2.x + v1.y * v2.y;
};

util.Vec2.cross = function (v1, v2)
{
	util.assertDefined(v1);
	util.assertDefined(v2);
    return v1.x * v2.y - v1.y * v2.x;
};

util.Vec2.prototype.rotate = function (angle)
{
	util.assertDefined(angle);
    var si = Math.sin(angle);
    var co = Math.cos(angle);

    return this.rotateSC(si, co);
};

util.Vec2.prototype.rotateSC = function (si, co)
{
	util.assertDefined(si);
	util.assertDefined(co);
    return new util.Vec2(this.x * co - this.y * si, this.x * si + this.y * co);
};

util.Vec2.angle = function (v1, v2)
{
	util.assertDefined(v1);
	util.assertDefined(v2);
    return Math.atan2(util.Vec2.cross(v1, v2), util.Vec2.dot(v1, v2));
};

util.Vec2.prototype.oxAngle = function ()
{
    return Math.atan2(this.y, this.x);
};

util.Vec2.diff = function (v1, v2)
{
	util.assertDefined(v1);
	util.assertDefined(v2);
    return new util.Vec2(v1.x - v2.x, v1.y - v2.y);
};

// assume arguments v1, f1, v2, f2, v3, f3, etc.
// where v[i] are vectors and f[i] are corresponding coefficients
util.Vec2.lc = function ()
{
    var v = new util.Vec2();
    for (var i = 0; i < arguments.length / 2; ++i)
        v = v.addScaled(arguments[2 * i], arguments[2 * i + 1]);
    return v;
};

util.Vec2.lc2 = function (v1, f1, v2, f2)
{
	util.assertDefined(v1);
	util.assertDefined(v2);
	util.assertDefined(f1);
	util.assertDefined(f2);
    return new util.Vec2(v1.x * f1 + v2.x * f2, v1.y * f1 + v2.y * f2);
};

util.Box2Abs = function ()
{
    if (arguments.length == 1 && 'min' in arguments[0] && 'max' in arguments[0])
    {
        this.p0 = arguments[0].min;
        this.p1 = arguments[0].max;
    }
    if (arguments.length == 2 && arguments[0] instanceof util.Vec2 && arguments[1] instanceof util.Vec2)
    {
        this.p0 = arguments[0];
        this.p1 = arguments[1];
    }
    else if (arguments.length == 4)
    {
        this.p0 = new util.Vec2(arguments[0], arguments[1]);
        this.p1 = new util.Vec2(arguments[2], arguments[3]);
    }
    else if (arguments.length == 0)
    {
        this.p0 = new util.Vec2();
        this.p1 = new util.Vec2();
    }
    else
        new Error("util.Box2Abs constructor only accepts 4 numbers or 2 vectors or no arguments!");
};

util.Box2Abs.prototype.toString = function () {
	return this.p0.toString() + " " + this.p1.toString();

}
util.Box2Abs.fromRelBox = function (relBox)
{
	util.assertDefined(relBox);
    return new util.Box2Abs(relBox.x, relBox.y,
    relBox.x + relBox.width, relBox.y + relBox.height);
};

util.Box2Abs.prototype.clone = function ()
{
    return new util.Box2Abs(this.p0, this.p1);
};

util.Box2Abs.union = function(/*util.Box2Abs*/b1, /*util.Box2Abs*/b2)
{
	util.assertDefined(b1);
	util.assertDefined(b2);
	return new util.Box2Abs(util.Vec2.min(b1.p0, b2.p0), util.Vec2.max(b1.p1, b2.p1));
};

util.Box2Abs.prototype.extend = function(/*util.Vec2*/lp, /*util.Vec2*/rb)
{
	util.assertDefined(lp);
	rb = rb || lp;
    return new util.Box2Abs(this.p0.sub(lp), this.p1.add(rb));
};

util.Box2Abs.prototype.include = function(/*util.Vec2*/p)
{
	util.assertDefined(p);
    return new util.Box2Abs(this.p0.min(p), this.p1.max(p));
};

util.Box2Abs.prototype.translate = function(/*util.Vec2*/d)
{
	util.assertDefined(d);
    // TODO [RB] ??? it does nothing... need to investigate and remove dead code that "uses" it
    // for now new implementation added
    // BEGIN
    /*
    this.p0.add(d);
    this.p1.add(d);
    */
    return new util.Box2Abs(this.p0.add(d), this.p1.add(d));
    // END
};

util.Box2Abs.prototype.transform = function(/*function(Vec2):Vec2*/f, context)
{
    return new util.Box2Abs(f.call(context, this.p0), f.call(context, this.p1));
};

util.Box2Abs.prototype.sz = function()
{
    return this.p1.sub(this.p0);
};

util.Box2Abs.prototype.pos = function()
{
    return this.p0;
};

// find intersection of a ray and a box and
//  return the shift magnitude to avoid it
util.Vec2.shiftRayBox =
    function (/*util.Vec2*/p, /*util.Vec2*/d, /*util.Box2Abs*/bb)
{
	util.assertDefined(p);
	util.assertDefined(d);
	util.assertDefined(bb);
    // four corner points of the box
    var b = [bb.p0, new util.Vec2(bb.p1.x, bb.p0.y),
            bb.p1, new util.Vec2(bb.p0.x, bb.p1.y)];
    var r = b.map(function(v){return v.sub(p)}); // b relative to p
    d = d.normalized();
    var rc = r.map(function(v){return util.Vec2.cross(v, d)}); // cross prods
    var rd = r.map(function(v){return util.Vec2.dot(v, d)}); // dot prods

    // find foremost points on the right and on the left of the ray
    var pid = -1, nid = -1;
    for (var i = 0; i < 4; ++i)
        if (rc[i] > 0)  {if (pid < 0 || rd[pid] < rd[i]) pid = i;}
        else            {if (nid < 0 || rd[nid] < rd[i]) nid = i;}

    if (nid < 0 || pid < 0) // no intersection, no shift
        return 0;

    // check the order
    var id0, id1;
    if (rd[pid] > rd[nid])
        id0 = nid, id1 = pid;
    else
        id0 = pid, id1 = nid;

    // simple proportion to calculate the shift
    return rd[id0] + Math.abs(rc[id0]) * (rd[id1] - rd[id0])
        / (Math.abs(rc[id0]) + Math.abs(rc[id1]));
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.util)
	util = {};

util.Set = {
	empty: function() {
		return {};
	},

	single: function(item) {
		var set = {};
		util.Set.add(set, item);
		return set;
	},

	size: function(set) {
		var cnt = 0;
		for (var id in set) {
			if (set[id] !== Object.prototype[id]) {
				cnt++;
			}
		}
		return cnt;
	},

	contains: function(set, v) {
		return typeof(set[v]) != "undefined" && set[v] !== Object.prototype[v];
	},

	subset: function(set1, set2) {
		for (var id in set1) {
			if (set1[id] !== Object.prototype[id]) {
				if (set2[id] !== set1[id]) {
					return false;
				}
			}
		}
		return true;
	},

	eq: function(set1, set2) {
		return util.Set.subset(set1, set2) && util.Set.subset(set2, set1);
	},

	each: function(set, func, context) {
		for (var v in set) {
			if (set[v] !== Object.prototype[v]) {
				func.call(context, set[v]);
			}
		}
	},

	filter: function(set, func, context) {
		var subset = {};
		for (var v in set) {
			if (set[v] !== Object.prototype[v]) {
				if (func.call(context, set[v])) {
					subset[set[v]] = set[v];
				}
			}
		}
		return subset;
	},

	pick: function(set) {
		for (var v in set) {
			if (set[v] !== Object.prototype[v]) {
				return set[v];
			}
		}
		return null;
	},

	list: function(set) {
		var list = [];
		for (var v in set) {
			if (set[v] !== Object.prototype[v]) {
				list.push(set[v]);
			}
		}
		return list;
	},

	add: function(set, item) {
		set[item] = item;
	},

	mergeIn: function(set, other) {
		util.Set.each(other, function(item){
			util.Set.add(set, item);
		});
	},

	remove: function(set, item) {
		var v = set[item];
		delete set[item];
		return v;
	},

	clone: function(other) {
		var set = {};
		util.Set.mergeIn(set, other);
		return set;
	},

	fromList: function(list) {
		var set = {};
		for (var i = 0; i < list.length; ++i) {
			set[list[i]-0] = list[i]-0;
		}
		return set;
	},

	keySetInt: function(map) {
		var set = {};
		map.each(function(id){
			set[id - 0] = id - 0;
		});
		return set;
	},

	find: function(set, func, context) {
		for (var v in set) {
			if (set[v] !== Object.prototype[v]) {
				if (func.call(context, set[v])) {
					return v;
				}
			}
		}
		return null;
	}
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.util)
    util = {};

util.Map = function (obj) {
    if (typeof(obj) != 'undefined' && obj.constructor != Object)
        throw Error("Passed object is not an instance of 'Object'!");
    this._obj = obj || {};
    this._count = 0;
};

util.Map.prototype.each = function(func, context) {
    for (var v in this._obj) {
        var v_int = parseInt(v);
        var value = this._obj[v];
        
        if (!isNaN(v_int))
            v = v_int;
        func.call(context, v, value)
    }
};

util.Map.prototype.find = function(func, context) {
    for (var v in this._obj) {
        var v_int = parseInt(v);
        var value = this._obj[v];
        
        if (!isNaN(v_int))
            v = v_int;
        if (func.call(context, v, value))
            return v;
    }
};

util.Map.prototype.findAll = function(func, context) {
    var vv = [];
    for (var v in this._obj) {
        var v_int = parseInt(v);
        var value = this._obj[v];
        if (!isNaN(v_int))
            v = v_int;
        if (func.call(context, v, value))
            vv.push(v);
    }
    return vv;
};

util.Map.prototype.keys = function() {
    var keys = [];
    for (var v in this._obj) {
        keys.push(v);
    }
    return keys;
};

util.Map.prototype.ikeys = function() {
    var keys = [];
    for (var v in this._obj) {
        keys.push(v - 0);
    }
    return keys;
};

util.Map.prototype.set = function (key, value) {
    this._count += (typeof(value) != 'undefined' ? 1 : 0)
        - (typeof(this._obj[key]) != 'undefined' ? 1 : 0);
    if (typeof(value) == 'undefined') {
        var val = this._obj[key];
        delete this._obj[key];
        return val;
    } else {
        return this._obj[key] = value;
    }
};

util.Map.prototype.get = function (key) {
    if (this._obj[key] !== Object.prototype[key])
        return this._obj[key];
    return undefined;
};

util.Map.prototype.has = function (key) {
    return (this._obj[key] !== Object.prototype[key]);
};

util.Map.prototype.unset = function (key) {
    return this.set(key, undefined);
};

util.Map.prototype.update = function (object) {
    for (var v in object)
        this.set(v, object[v]);
};

util.Map.prototype.clear = function () {
    this._obj = {};
};

util.Map.prototype.count = function () {
    return this._count;
};

util.Map.prototype.idList = function () {
    return util.idList(this._obj);
};

util.Map.prototype.keyOf = function(value) {
    for (var key in this._obj) if (this._obj[key] == value) return key;
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.util || !util.Map)
    throw new Error("Map should be defined first");

util.Pool = function ()
{
    this._map = new util.Map();
    this._nextId = 0;
};

util.Pool.prototype.newId = function ()
{
    return this._nextId++;
};

util.Pool.prototype.add = function (obj)
{
    var id = this._nextId++;
    this._map.set(id, obj);
    return id;
};

util.Pool.prototype.set = function (id, obj)
{
    this._map.set(id, obj);
};

util.Pool.prototype.get = function (id)
{
    return this._map.get(id);
};

util.Pool.prototype.has = function (id) {
    return this._map.has(id);
};

util.Pool.prototype.remove = function (id)
{
    return this._map.unset(id);
};

util.Pool.prototype.clear = function ()
{
    this._map.clear();
};

util.Pool.prototype.keys = function ()
{
	return this._map.keys();
};

util.Pool.prototype.ikeys = function ()
{
	return this._map.ikeys();
};

util.Pool.prototype.each = function (func, context)
{
    this._map.each(func, context);
};

util.Pool.prototype.find = function (func, context)
{
    return this._map.find(func, context);
};

util.Pool.prototype.count = function ()
{
    return this._map.count();
};

util.Pool.prototype.keyOf = function(value) {
    return this._map.keyOf(value);
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.util || !util.Vec2)
    throw new Error("Vec2 should be defined first");

if (!window.chem)
    chem = {};

function hexToRGB(hex) {
	return {
		'r': parseInt(hex.substring(1,3), 16),
		'g': parseInt(hex.substring(3,5), 16),
		'b': parseInt(hex.substring(5,7), 16)
	};
}

function rgbCompToHex(c) {
	c = c.toFixed();
	c = Math.max(Math.min(c, 255),0);
	var hex = c.toString(16);
	if (hex.length < 2)
		hex = '0' + hex;
	return hex;
}

function rgbToHex(rgb) {
	return '#' + 
		rgbCompToHex(rgb.r) +
		rgbCompToHex(rgb.g) +
		rgbCompToHex(rgb.b);
}

function rgbRescale(rgb, maxNorm) {
	var norm = 0.21 * rgb.r + 0.72 * rgb.g + 0.07 * rgb.b;
	if (norm <= maxNorm)
		return rgb;
	return {
		'r': (rgb.r * maxNorm / norm).toFixed()-0,
		'g': (rgb.g * maxNorm / norm).toFixed()-0,
		'b': (rgb.b * maxNorm / norm).toFixed()-0
	}
}

// element table and utilities
chem.Element = function (label, period, group, putHydrogenOnTheLeft, color, ypos, xpos)
{
    this.label = label;
    this.period = period;
    this.group = group;
    this.putHydrogenOnTheLeft = putHydrogenOnTheLeft;
    this.color = color || '#000000';
    this.labelColor = rgbToHex(rgbRescale(hexToRGB(this.color),150));
	this.xpos = xpos || group;
	this.ypos = ypos || period;
	
	var r = (("0x"+this.color.substring(1,3)) - 0)/255;
	var g = (("0x"+this.color.substring(3,5)) - 0)/255;
	var b = (("0x"+this.color.substring(5,7)) - 0)/255;
	var luminance = 0.299*r + 0.587*g + 0.114*b;
	if (luminance > 0.6) {
		r *= 0.6 / luminance;
		g *= 0.6 / luminance;
		b *= 0.6 / luminance;
	}
	r = Math.ceil(Math.min(r * 255, 255)).toString(16);
	g = Math.ceil(Math.min(g * 255, 255)).toString(16);
	b = Math.ceil(Math.min(b * 255, 255)).toString(16);
	r = r.length == 1 ? "0" + r : r;
	g = g.length == 1 ? "0" + g : g;
	b = b.length == 1 ? "0" + b : b;
	this.color = "#" + r + g + b;

};

chem.Element.elements = new util.Map({
      1: new chem.Element( 'H', 1, 1, false, '#000000', 1, 1),
      2: new chem.Element('He', 1, 8, false, '#d9ffff', 1, 18),
      3: new chem.Element('Li', 2, 1, false, '#cc80ff', 2, 1),
      4: new chem.Element('Be', 2, 2, false, '#c2ff00', 2, 2),
      5: new chem.Element( 'B', 2, 3, false, '#ffb5b5', 2,13),
      6: new chem.Element( 'C', 2, 4, false, '#000000', 2,14),
      7: new chem.Element( 'N', 2, 5, false, '#304ff7', 2,15),
      8: new chem.Element( 'O', 2, 6, true,  '#ff0d0d', 2,16),
      9: new chem.Element( 'F', 2, 7, true,  '#8fe04f', 2,17),
     10: new chem.Element('Ne', 2, 8, false, '#b3e3f5', 2,18),
     11: new chem.Element('Na', 3, 1, false, '#ab5cf2', 3, 1),
     12: new chem.Element('Mg', 3, 2, false, '#8aff00', 3, 2),
     13: new chem.Element('Al', 3, 3, false, '#bfa6a6', 3,13),
     14: new chem.Element('Si', 3, 4, false, '#f0c7a1', 3,14),
     15: new chem.Element( 'P', 3, 5, false, '#ff8000', 3,15),
     16: new chem.Element( 'S', 3, 6, true,  '#d9a61a', 3,16),
     17: new chem.Element('Cl', 3, 7, true,  '#1fd01f', 3,17),
     18: new chem.Element('Ar', 3, 8, false, '#80d1e3', 3,18),
     19: new chem.Element( 'K', 4, 1, false, '#8f40d4', 4, 1),
     20: new chem.Element('Ca', 4, 2, false, '#3dff00', 4, 2),
     21: new chem.Element('Sc', 4, 3, false, '#e6e6e6', 4, 3),
     22: new chem.Element('Ti', 4, 4, false, '#bfc2c7', 4, 4),
     23: new chem.Element( 'V', 4, 5, false, '#a6a6ab', 4, 5),
     24: new chem.Element('Cr', 4, 6, false, '#8a99c7', 4, 6),
     25: new chem.Element('Mn', 4, 7, false, '#9c7ac7', 4, 7),
     26: new chem.Element('Fe', 4, 8, false, '#e06633', 4, 8),
     27: new chem.Element('Co', 4, 8, false, '#f08fa1', 4, 9),
     28: new chem.Element('Ni', 4, 8, false, '#4fd14f', 4,10),
     29: new chem.Element('Cu', 4, 1, false, '#c78033', 4,11),
     30: new chem.Element('Zn', 4, 2, false, '#7d80b0', 4,12),
     31: new chem.Element('Ga', 4, 3, false, '#c28f8f', 4,13),
     32: new chem.Element('Ge', 4, 4, false, '#668f8f', 4,14),
     33: new chem.Element('As', 4, 5, false, '#bd80e3', 4,15),
     34: new chem.Element('Se', 4, 6, true,  '#ffa100', 4,16),
     35: new chem.Element('Br', 4, 7, true,  '#a62929', 4,17),
     36: new chem.Element('Kr', 4, 8, false, '#5cb8d1', 4,18),
     37: new chem.Element('Rb', 5, 1, false, '#702eb0', 5, 1),
     38: new chem.Element('Sr', 5, 2, false, '#00ff00', 5, 2),
     39: new chem.Element( 'Y', 5, 3, false, '#94ffff', 5, 3),
     40: new chem.Element('Zr', 5, 4, false, '#94e0e0', 5, 4),
     41: new chem.Element('Nb', 5, 5, false, '#73c2c9', 5, 5),
     42: new chem.Element('Mo', 5, 6, false, '#54b5b5', 5, 6),
     43: new chem.Element('Tc', 5, 7, false, '#3b9e9e', 5, 7),
     44: new chem.Element('Ru', 5, 8, false, '#248f8f', 5, 8),
     45: new chem.Element('Rh', 5, 8, false, '#0a7d8c', 5, 9),
     46: new chem.Element('Pd', 5, 8, false, '#006985', 5,10),
     47: new chem.Element('Ag', 5, 1, false, '#bfbfbf', 5,11),
     48: new chem.Element('Cd', 5, 2, false, '#ffd98f', 5,12),
     49: new chem.Element('In', 5, 3, false, '#a67573', 5,13),
     50: new chem.Element('Sn', 5, 4, false, '#668080', 5,14),
     51: new chem.Element('Sb', 5, 5, false, '#9e63b5', 5,15),
     52: new chem.Element('Te', 5, 6, false, '#d47a00', 5,16),
     53: new chem.Element( 'I', 5, 7, true,  '#940094', 5,17),
     54: new chem.Element('Xe', 5, 8, false, '#429eb0', 5,18),
     55: new chem.Element('Cs', 6, 1, false, '#57178f', 6, 1),
     56: new chem.Element('Ba', 6, 2, false, '#00c900', 6, 2),
     57: new chem.Element('La', 6, 3, false, '#70d4ff', 6, 3),
     58: new chem.Element('Ce', 6, 3, false, '#ffffc7', 8, 4),
     59: new chem.Element('Pr', 6, 3, false, '#d9ffc7', 8, 5),
     60: new chem.Element('Nd', 6, 3, false, '#c7ffc7', 8, 6),
     61: new chem.Element('Pm', 6, 3, false, '#a3ffc7', 8, 7),
     62: new chem.Element('Sm', 6, 3, false, '#8fffc7', 8, 8),
     63: new chem.Element('Eu', 6, 3, false, '#61ffc7', 8, 9),
     64: new chem.Element('Gd', 6, 3, false, '#45ffc7', 8,10),
     65: new chem.Element('Tb', 6, 3, false, '#30ffc7', 8,11),
     66: new chem.Element('Dy', 6, 3, false, '#1fffc7', 8,12),
     67: new chem.Element('Ho', 6, 3, false, '#00ff9c', 8,13),
     68: new chem.Element('Er', 6, 3, false, '#00e675', 8,14),
     69: new chem.Element('Tm', 6, 3, false, '#00d452', 8,15),
     70: new chem.Element('Yb', 6, 3, false, '#00bf38', 8,16),
     71: new chem.Element('Lu', 6, 3, false, '#00ab24', 8,17),
     72: new chem.Element('Hf', 6, 4, false, '#4dc2ff', 6, 4),
     73: new chem.Element('Ta', 6, 5, false, '#4da6ff', 6, 5),
     74: new chem.Element( 'W', 6, 6, false, '#2194d6', 6, 6),
     75: new chem.Element('Re', 6, 7, false, '#267dab', 6, 7),
     76: new chem.Element('Os', 6, 8, false, '#266696', 6, 8),
     77: new chem.Element('Ir', 6, 8, false, '#175487', 6, 9),
     78: new chem.Element('Pt', 6, 8, false, '#d1d1e0', 6,10),
     79: new chem.Element('Au', 6, 1, false, '#ffd124', 6,11),
     80: new chem.Element('Hg', 6, 2, false, '#b8b8d1', 6,12),
     81: new chem.Element('Tl', 6, 3, false, '#a6544d', 6,13),
     82: new chem.Element('Pb', 6, 4, false, '#575961', 6,14),
     83: new chem.Element('Bi', 6, 5, false, '#9e4fb5', 6,15),
     84: new chem.Element('Po', 6, 6, false, '#ab5c00', 6,16),
     85: new chem.Element('At', 6, 7, false, '#754f45', 6,17),
     86: new chem.Element('Rn', 6, 8, false, '#428296', 6,18),
     87: new chem.Element('Fr', 7, 1, false, '#420066', 7, 1),
     88: new chem.Element('Ra', 7, 2, false, '#007d00', 7, 2),
     89: new chem.Element('Ac', 7, 3, false, '#70abfa', 7, 3),
     90: new chem.Element('Th', 7, 3, false, '#00baff', 9, 4),
     91: new chem.Element('Pa', 7, 3, false, '#00a1ff', 9, 5),
     92: new chem.Element( 'U', 7, 3, false, '#008fff', 9, 6),
     93: new chem.Element('Np', 7, 3, false, '#0080ff', 9, 7),
     94: new chem.Element('Pu', 7, 3, false, '#006bff', 9, 8),
     95: new chem.Element('Am', 7, 3, false, '#545cf2', 9, 9),
     96: new chem.Element('Cm', 7, 3, false, '#785ce3', 9,10),
     97: new chem.Element('Bk', 7, 3, false, '#8a4fe3', 9,11),
     98: new chem.Element('Cf', 7, 3, false, '#a136d4', 9,12),
     99: new chem.Element('Es', 7, 3, false, '#b31fd4', 9,13),
    // TODO need to fix colors for the elements below
    100: new chem.Element('Fm', 7, 3, false, '#000000', 9,14),
    101: new chem.Element('Md', 7, 3, false, '#000000', 9,15),
    102: new chem.Element('No', 7, 3, false, '#000000', 9,16),
    103: new chem.Element('Lr', 7, 3, false, '#000000', 9,17),
    104: new chem.Element('Rf', 7, 4, false, '#4dc2ff', 7, 4),
    105: new chem.Element('Db', 7, 5, false, '#4da6ff', 7, 5),
    106: new chem.Element('Sg', 7, 6, false, '#2194d6', 7, 6),
    107: new chem.Element('Bh', 7, 7, false, '#267dab', 7, 7),
    108: new chem.Element('Hs', 7, 8, false, '#266696', 7, 8),
    109: new chem.Element('Mt', 7, 8, false, '#175487', 7, 9),
    110: new chem.Element('Ds', 7, 8, false, '#d1d1e0', 7,10),
    111: new chem.Element('Rg', 7, 1, false, '#ffd124', 7,11),
    112: new chem.Element('Cn', 7, 2, false, '#b8b8d1', 7,12)
});

chem.Element.labelMap = null;

chem.Element.getElementByLabel = function (label)
{
    if (!this.labelMap)
    {
        this.labelMap = {};
        this.elements.each(function(key, value){
            this.labelMap[value.label] = key-0;
        }, this);
    }
    if (!this.labelMap.hasOwnProperty(label))
        return null;
    return this.labelMap[label];
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

// chem.Struct constructor and utilities are defined here
if (!window.chem || !util.Vec2 || !util.Pool)
	throw new Error("Vec2, Pool should be defined first");

chem.Struct = function ()
{
	this.atoms = new util.Pool();
	this.bonds = new util.Pool();
	this.sgroups = new util.Pool();
	this.halfBonds = new util.Map();
	this.loops = new util.Pool();
	this.isChiral = false;
	this.isReaction = false;
	this.rxnArrows = new util.Pool();
	this.rxnPluses = new util.Pool();
    this.frags = new util.Pool();
    this.rgroups = new util.Map();
};

chem.Struct.prototype.isBlank = function ()
{
	return this.atoms.count() == 0 &&
		this.rxnArrows.count() == 0 &&
		this.rxnPluses.count() == 0 &&
		!this.isChiral;
};

chem.Struct.prototype.toLists = function ()
{
	var aidMap = {};
	var atomList = [];
	this.atoms.each(function(aid, atom) {
		aidMap[aid] = atomList.length;
		atomList.push(atom);
	});

	var bondList = [];
	this.bonds.each(function(bid, bond) {
		var b = new chem.Struct.Bond(bond);
		b.begin = aidMap[bond.begin];
		b.end = aidMap[bond.end];
		bondList.push(b);
	});

	return {
		'atoms': atomList,
		'bonds': bondList
	};
};

chem.Struct.prototype.clone = function (atomSet, bondSet, dropRxnSymbols)
{
	var cp = new chem.Struct();
	return this.mergeInto(cp, atomSet, bondSet, dropRxnSymbols);
};

chem.Struct.prototype.getScaffold = function () {
    var atomSet = util.Set.empty();
    this.atoms.each(function(aid){
        util.Set.add(atomSet, aid);
    }, this);
    this.rgroups.each(function(rgid, rg){
        rg.frags.each(function(fnum, fid) {
            this.atoms.each(function(aid, atom){
                if (atom.fragment == fid) {
                    util.Set.remove(atomSet, aid);
                }
            }, this);
        }, this);
    }, this);
	return this.clone(atomSet);
};

chem.Struct.prototype.getFragment = function (fid) {
    var atomSet = util.Set.empty();
    this.atoms.each(function(aid, atom){
        if (atom.fragment == fid) {
            util.Set.add(atomSet, aid);
        }
    }, this);
	return this.clone(atomSet);
}

chem.Struct.prototype.mergeInto = function (cp, atomSet, bondSet, dropRxnSymbols, keepAllRGroups)
{
	atomSet = atomSet || util.Set.keySetInt(this.atoms);
	bondSet = bondSet || util.Set.keySetInt(this.bonds);
	bondSet = util.Set.filter(bondSet, function(bid){
		var bond = this.bonds.get(bid);
		return util.Set.contains(atomSet, bond.begin) && util.Set.contains(atomSet, bond.end);
	}, this);

    var fidMask = {};
	this.atoms.each(function(aid, atom) {
        if (util.Set.contains(atomSet, aid))
            fidMask[atom.fragment] = 1;
	});
    var fidMap = {};
    this.frags.each(function(fid, frag) {
        if (fidMask[fid])
            fidMap[fid] = cp.frags.add(frag.clone());
    });

    this.rgroups.each(function(rgid, rgroup) {
        var keepGroup = keepAllRGroups;
        if (!keepGroup) {
            rgroup.frags.each(function(fnum, fid) {
                if (fidMask[fid])
                    keepGroup = true;
            });
            if (!keepGroup)
                return;
        }
        var rg = cp.rgroups.get(rgid);
        if (rg) {
            rgroup.frags.each(function(fnum, fid) {
                if (fidMask[fid])
                    rg.frags.add(fidMap[fid]);
            });
        } else {
            cp.rgroups.set(rgid, rgroup.clone(fidMap));
        }
    });

	var aidMap = {};
	this.atoms.each(function(aid, atom) {
		if (util.Set.contains(atomSet, aid))
			aidMap[aid] = cp.atoms.add(atom.clone(fidMap));
	});

	var bidMap = {};
	this.bonds.each(function(bid, bond) {
		if (util.Set.contains(bondSet, bid))
			bidMap[bid] = cp.bonds.add(bond.clone(aidMap));
	});

	this.sgroups.each(function(sid, sg) {
		var i;
		for (i = 0; i < sg.atoms.length; ++i)
			if (!util.Set.contains(atomSet, sg.atoms[i]))
				return;
		sg = chem.SGroup.clone(sg, aidMap, bidMap);
		var id = cp.sgroups.add(sg);
		sg.id = id;
		for (i = 0; i < sg.atoms.length; ++i) {
			util.Set.add(cp.atoms.get(sg.atoms[i]).sgs, id);
		}
	});
	cp.isChiral = this.isChiral;
	if (!dropRxnSymbols) {
		cp.isReaction = this.isReaction;
		this.rxnArrows.each(function(id, item) {
			cp.rxnArrows.add(item.clone());
		});
		this.rxnPluses.each(function(id, item) {
			cp.rxnPluses.add(item.clone());
		});
	}
	return cp;
};

chem.Struct.prototype.findBondId = function (begin, end)
{
	var id = -1;

	this.bonds.find(function (bid, bond)
	{
		if ((bond.begin == begin && bond.end == end) ||
			(bond.begin == end && bond.end == begin))
			{
			id = bid;
			return true;
		}
		return false;
	}, this);

	return id;
};

chem.Struct.ATOM =
{
	RADICAL:
	{
		NONE:    0,
		SINGLET: 1,
		DOUPLET: 2,
		TRIPLET: 3
	}
};

chem.Struct.radicalElectrons = function(radical)
{
	radical = radical - 0;
	if (radical == chem.Struct.ATOM.RADICAL.NONE)
		return 0;
	else if (radical == chem.Struct.ATOM.RADICAL.DOUPLET)
		return 1;
	else if (radical == chem.Struct.ATOM.RADICAL.SINGLET ||
		radical == chem.Struct.ATOM.RADICAL.TRIPLET)
		return 2;
	throw new Error("Unknown radical value");
};

chem.Struct.BOND =
{
	TYPE:
	{
		SINGLE: 1,
		DOUBLE: 2,
		TRIPLE: 3,
		AROMATIC: 4,
		SINGLE_OR_DOUBLE: 5,
		SINGLE_OR_AROMATIC: 6,
		DOUBLE_OR_AROMATIC: 7,
		ANY : 8
	},

	STEREO:
	{
		NONE: 0,
		UP: 1,
		EITHER: 4,
		DOWN: 6,
		CIS_TRANS: 3
	},

	TOPOLOGY:
	{
		EITHER: 0,
		RING: 1,
		CHAIN: 2
	},

	REACTING_CENTER:
	{
	   NOT_CENTER: -1,
	   UNMARKED: 0,
	   CENTER: 1,
	   UNCHANGED: 2,
	   MADE_OR_BROKEN: 4,
	   ORDER_CHANGED: 8,
	   MADE_OR_BROKEN_AND_CHANGED: 12
	}
};

chem.Struct.FRAGMENT = {
	NONE:0,
	REACTANT:1,
	PRODUCT:2,
	AGENT:3
};

chem.Struct.Atom = function (params)
{
	if (!params || !('label' in params))
		throw new Error("label must be specified!");

	this.label = params.label;
        this.fragment = !Object.isUndefined(params.fragment) ? params.fragment : -1;

	util.ifDef(this, params, 'isotope', 0);
	util.ifDef(this, params, 'radical', 0);
	util.ifDef(this, params, 'charge', 0);
	util.ifDef(this, params, 'valence', 0);
    util.ifDef(this, params, 'rglabel', null); // r-group index mask, i-th bit stands for i-th r-site
    util.ifDef(this, params, 'attpnt', null); // attachment point
	util.ifDef(this, params, 'explicitValence', 0);
	util.ifDef(this, params, 'implicitH', 0);
	if (!Object.isUndefined(params.pp))
		this.pp = new util.Vec2(params.pp);
	else
		this.pp = new util.Vec2();

        // sgs should only be set when an atom is added to an s-group by an appropriate method,
        //   or else a copied atom might think it belongs to a group, but the group be unaware of the atom
        // TODO: make a consistency check on atom/s-group assignments
	this.sgs = {};

	// query
	util.ifDef(this, params, 'ringBondCount', 0);
	util.ifDef(this, params, 'substitutionCount', 0);
	util.ifDef(this, params, 'unsaturatedAtom', 0);
	util.ifDef(this, params, 'hCount', 0);

	// reaction
    util.ifDef(this, params, 'aam', 0);
	util.ifDef(this, params, 'invRet', 0);
	util.ifDef(this, params, 'exactChangeFlag', 0);
	util.ifDef(this, params, 'rxnFragmentType', -1);

	this.atomList = !Object.isUndefined(params.atomList) && params.atomList != null ? new chem.Struct.AtomList(params.atomList) : null;
	this.neighbors = []; // set of half-bonds having this atom as their origin
	this.badConn = false;
};

chem.Struct.Atom.getAttrHash = function(atom) {
    var attrs = new Hash();
	for (var attr in chem.Struct.Atom.attrlist) {
		if (typeof(atom[attr]) != 'undefined') {
			attrs.set(attr, atom[attr]);
		}
	}
	return attrs;
};

chem.Struct.Atom.attrlist = {
    'label':0,
	'isotope':0,
	'radical':0,
	'charge':0,
	'valence':0,
	'explicitValence':0,
	'implicitH':0,
	'ringBondCount':0,
	'substitutionCount':0,
	'unsaturatedAtom':0,
	'hCount':0,
	'atomList':null,
    'rglabel':null,
    'attpnt':null,
    'aam':0
};

chem.Struct.Atom.prototype.clone = function(fidMap)
{
    var ret = new chem.Struct.Atom(this);
    if (fidMap && this.fragment in fidMap) {
        ret.fragment = fidMap[this.fragment];
    }
    return ret;
};

chem.Struct.Atom.prototype.isQuery =  function ()
{
	return this.atomList != null || this.label == 'A';
};

chem.Struct.Atom.prototype.pureHydrogen =  function ()
{
	return this.label == 'H' && this.isotope == 0;
};

chem.Struct.Atom.prototype.isPlainCarbon =  function ()
{
	return this.label == 'C' && this.isotope == 0 && this.isotope == 0 &&
		this.radical == 0 && this.charge == 0 && this.explicitValence == 0 &&
		this.ringBondCount == 0 && this.substitutionCount == 0 && this.unsaturatedAtom == 0 && this.hCount == 0 &&
		!this.atomList;
};

chem.Struct.AtomList = function (params)
{
	if (!params || !('notList' in params) || !('ids' in params))
		throw new Error("'notList' and 'ids' must be specified!");

	this.notList = params.notList; /*boolean*/
	this.ids = params.ids; /*Array of integers*/
};

chem.Struct.AtomList.prototype.labelList = function ()
{
	var labels = [];
	for (var i = 0; i < this.ids.length; ++i)
		labels.push(chem.Element.elements.get(this.ids[i]).label);
	return labels;
};

chem.Struct.AtomList.prototype.label = function ()
{
	var label = "[" + this.labelList().join(",") + "]";
	if (this.notList)
		label = "!" + label;
	return label;
};

chem.Struct.Bond = function (params)
{
	if (!params || !('begin' in params) || !('end' in params) || !('type' in params))
		throw new Error("'begin', 'end' and 'type' properties must be specified!");

	this.begin = params.begin;
	this.end = params.end;
	this.type = params.type;
	util.ifDef(this, params, 'stereo', chem.Struct.BOND.STEREO.NONE);
	util.ifDef(this, params, 'topology', chem.Struct.BOND.TOPOLOGY.EITHER);
	util.ifDef(this, params, 'reactingCenterStatus', 0);
	this.hb1 = null; // half-bonds
	this.hb2 = null;
	this.len = 0;
	this.center = new util.Vec2();
	this.sb = 0;
	this.sa = 0;
	this.angle = 0;
};

chem.Struct.Bond.prototype.clone = function (aidMap)
{
	var cp = new chem.Struct.Bond(this);
	if (aidMap) {
		cp.begin = aidMap[cp.begin];
		cp.end = aidMap[cp.end];
	}
	return cp;
};

chem.Struct.Bond.prototype.findOtherEnd = function (i)
{
	if (i == this.begin)
		return this.end;
	if (i == this.end)
		return this.begin;
	throw new Error("bond end not found");
};

chem.HalfBond = function (/*num*/begin, /*num*/end, /*num*/bid)
{
	if (arguments.length != 3)
		throw new Error("Invalid parameter number!");

	this.begin = begin - 0;
	this.end = end - 0;
	this.bid = bid - 0;

	// rendering properties
	this.dir = new util.Vec2(); // direction
	this.norm = new util.Vec2(); // left normal
	this.ang = 0; // angle to (1,0), used for sorting the bonds
	this.p = new util.Vec2(); // corrected origin position
	this.loop = -1; // left loop id if the half-bond is in a loop, otherwise -1
	this.contra = -1; // the half bond contrary to this one
	this.next = -1; // the half-bond next ot this one in CCW order
	this.leftSin = 0;
	this.leftCos = 0;
	this.leftNeighbor = 0;
	this.rightSin = 0;
	this.rightCos = 0;
	this.rightNeighbor = 0;
};

chem.Struct.prototype.initNeighbors = function ()
{
	this.atoms.each(function(aid, atom){
		atom.neighbors = [];
	});
	this.bonds.each(function(bid, bond){
		var a1 = this.atoms.get(bond.begin);
		var a2 = this.atoms.get(bond.end);
		a1.neighbors.push(bond.hb1);
		a2.neighbors.push(bond.hb2);
	}, this);
};

chem.Struct.prototype.bondInitHalfBonds = function (bid, /*opt*/ bond)
{
	bond = bond || this.bonds.get(bid);
	bond.hb1 = 2 * bid;
	bond.hb2 = 2 * bid + 1;
	this.halfBonds.set(bond.hb1, new chem.HalfBond(bond.begin, bond.end, bid));
	this.halfBonds.set(bond.hb2, new chem.HalfBond(bond.end, bond.begin, bid));
	var hb1 = this.halfBonds.get(bond.hb1);
	var hb2 = this.halfBonds.get(bond.hb2);
	hb1.contra = bond.hb2;
	hb2.contra = bond.hb1;
};

chem.Struct.prototype.halfBondUpdate = function (hbid)
{
	var hb = this.halfBonds.get(hbid);
	var p1 = this.atoms.get(hb.begin).pp;
	var p2 = this.atoms.get(hb.end).pp;
	var d = util.Vec2.diff(p2, p1).normalized();
	hb.dir = d;
	hb.norm = d.turnLeft();
	hb.ang = hb.dir.oxAngle();
	if (hb.loop < 0)
		hb.loop = -1;
};

chem.Struct.prototype.initHalfBonds = function ()
{
	this.halfBonds.clear();
	this.bonds.each(this.bondInitHalfBonds, this);
};

chem.Struct.prototype.setHbNext = function (hbid, next)
{
	this.halfBonds.get(this.halfBonds.get(hbid).contra).next = next;
};

chem.Struct.prototype.halfBondSetAngle = function (hbid, left)
{
	var hb = this.halfBonds.get(hbid);
	var hbl = this.halfBonds.get(left);
	hbl.rightCos = hb.leftCos = util.Vec2.dot(hbl.dir, hb.dir);
	hbl.rightSin = hb.leftSin = util.Vec2.cross(hbl.dir, hb.dir);
	hb.leftNeighbor = left;
	hbl.rightNeighbor = hbid;
};

chem.Struct.prototype.atomAddNeighbor = function (hbid)
{
	var hb = this.halfBonds.get(hbid);
	var atom = this.atoms.get(hb.begin);
	var i = 0;
	for (i = 0; i < atom.neighbors.length; ++i)
		if (this.halfBonds.get(atom.neighbors[i]).ang > hb.ang)
			break;
	atom.neighbors.splice(i, 0, hbid);
	var ir = atom.neighbors[(i + 1) % atom.neighbors.length];
	var il = atom.neighbors[(i + atom.neighbors.length - 1)
	% atom.neighbors.length];
	this.setHbNext(il, hbid);
	this.setHbNext(hbid, ir);
	this.halfBondSetAngle(hbid, il);
	this.halfBondSetAngle(ir, hbid);
};

chem.Struct.prototype.atomSortNeighbors = function (aid) {
	var atom = this.atoms.get(aid);
	atom.neighbors = atom.neighbors.sortBy(function(nei){
		return this.halfBonds.get(nei).ang;
	}, this);

	var i;
	for (i = 0; i < atom.neighbors.length; ++i)
		this.halfBonds.get(this.halfBonds.get(atom.neighbors[i]).contra).next =
		atom.neighbors[(i + 1) % atom.neighbors.length];
	for (i = 0; i < atom.neighbors.length; ++i)
		this.halfBondSetAngle(atom.neighbors[(i + 1) % atom.neighbors.length],
			atom.neighbors[i]);
};

chem.Struct.prototype.atomUpdateHalfBonds = function (aid) {
	var nei = this.atoms.get(aid).neighbors;
	for (var i = 0; i < nei.length; ++i) {
		var hbid = nei[i];
		this.halfBondUpdate(hbid);
		this.halfBondUpdate(this.halfBonds.get(hbid).contra);
	}
};

chem.Struct.prototype.sGroupsRecalcCrossBonds = function () {
	this.sgroups.each(function(sgid, sg){
		sg.xBonds = [];
		sg.neiAtoms = [];
	},this);
	this.bonds.each(function(bid, bond){
		var a1 = this.atoms.get(bond.begin);
		var a2 = this.atoms.get(bond.end);
		util.Set.each(a1.sgs, function(sgid){
			if (!util.Set.contains(a2.sgs, sgid)) {
				var sg = this.sgroups.get(sgid);
				sg.xBonds.push(bid);
				util.arrayAddIfMissing(sg.neiAtoms, bond.end);
			}
		}, this);
		util.Set.each(a2.sgs, function(sgid){
			if (!util.Set.contains(a1.sgs, sgid)) {
				var sg = this.sgroups.get(sgid);
				sg.xBonds.push(bid);
				util.arrayAddIfMissing(sg.neiAtoms, bond.begin);
			}
		}, this);
	},this);
};

chem.Struct.prototype.sGroupDelete = function (sgid)
{
	var sg = this.sgroups.get(sgid);
	for (var i = 0; i < sg.atoms.length; ++i) {
		util.Set.remove(this.atoms.get(sg.atoms[i]).sgs, sgid);
	}
	this.sgroups.remove(sgid);
};

chem.Struct.itemSetPos = function (item, pp) // TODO: remove
{
	item.pp = pp;
};

chem.Struct.prototype._itemSetPos = function (map, id, pp, scaleFactor)
{
	chem.Struct.itemSetPos(this[map].get(id), pp, scaleFactor);
};

chem.Struct.prototype._atomSetPos = function (id, pp, scaleFactor)
{
	this._itemSetPos('atoms', id, pp, scaleFactor);
};

chem.Struct.prototype._rxnPlusSetPos = function (id, pp, scaleFactor)
{
	this._itemSetPos('rxnPluses', id, pp, scaleFactor);
};

chem.Struct.prototype._rxnArrowSetPos = function (id, pp, scaleFactor)
{
	this._itemSetPos('rxnArrows', id, pp, scaleFactor);
};

chem.Struct.prototype.getCoordBoundingBox = function (atomSet)
{
	var bb = null;
	var extend = function(pp) {
		if (!bb)
			bb = {
				min: pp,
				max: pp
			};
		else {
			bb.min = util.Vec2.min(bb.min, pp);
			bb.max = util.Vec2.max(bb.max, pp);
		}
	};

	var global = typeof(atomSet) == 'undefined';

	this.atoms.each(function (aid, atom) {
		if (global || util.Set.contains(atomSet, aid))
			extend(atom.pp);
	});
	if (global) {
		this.rxnPluses.each(function (id, item) {
			extend(item.pp);
		});
		this.rxnArrows.each(function (id, item) {
			extend(item.pp);
		});
	}
	if (!bb && global)
		bb = {
			min: new util.Vec2(0, 0),
			max: new util.Vec2(1, 1)
		};
	return bb;
};

chem.Struct.prototype.getCoordBoundingBoxObj = function ()
{
	var bb = null;
	var extend = function(pp) {
		if (!bb)
			bb = {
				min: new util.Vec2(pp),
				max: new util.Vec2(pp)
			};
		else {
			bb.min = util.Vec2.min(bb.min, pp);
			bb.max = util.Vec2.max(bb.max, pp);
		}
	};

	this.atoms.each(function (aid, atom) {
		extend(atom.pp);
	});
	return bb;
};

chem.Struct.prototype.getBondLengthData = function ()
{
	var totalLength = 0;
	var cnt = 0;
	this.bonds.each(function(bid, bond){
		totalLength += util.Vec2.dist(
			this.atoms.get(bond.begin).pp,
			this.atoms.get(bond.end).pp);
		cnt++;
	}, this);
	return {cnt:cnt, totalLength:totalLength};
};

chem.Struct.prototype.getAvgBondLength = function ()
{
    var bld = this.getBondLengthData();
    return bld.cnt > 0 ? bld.totalLength / bld.cnt : -1;
};

chem.Struct.prototype.getAvgClosestAtomDistance = function ()
{
	var totalDist = 0, minDist, dist = 0;
	var keys = this.atoms.keys(), k, j;
	for (k = 0; k < keys.length; ++k) {
		minDist = -1;
		for (j = 0; j < keys.length; ++j) {
			if (j == k)
				continue;
			dist = util.Vec2.dist(this.atoms.get(keys[j]).pp, this.atoms.get(keys[k]).pp);
			if (minDist < 0 || minDist > dist)
				minDist = dist;
		}
		totalDist += minDist;
	}

	return keys.length > 0 ? totalDist / keys.length : -1;
};

chem.Struct.prototype.checkBondExists = function (begin, end)
{
	var bondExists = false;
	this.bonds.each(function(bid, bond){
		if ((bond.begin == begin && bond.end == end) ||
			(bond.end == begin && bond.begin == end))
			bondExists = true;
	}, this);
	return bondExists;
};

chem.Loop = function (/*Array of num*/hbs, /*Struct*/struct, /*bool*/convex)
{
	this.hbs = hbs; // set of half-bonds involved
	this.dblBonds = 0; // number of double bonds in the loop
	this.aromatic = true;
	this.convex = convex || false;

	hbs.each(function(hb){
		var bond = struct.bonds.get(struct.halfBonds.get(hb).bid);
		if (bond.type != chem.Struct.BOND.TYPE.AROMATIC)
			this.aromatic = false;
		if (bond.type == chem.Struct.BOND.TYPE.DOUBLE)
			this.dblBonds++;
	}, this);
};

chem.Struct.RxnPlus = function (params)
{
	params = params || {};
	this.pp = params.pp ? new util.Vec2(params.pp) : new util.Vec2();
};

chem.Struct.RxnPlus.prototype.clone = function ()
{
	return new chem.Struct.RxnPlus(this);
};

chem.Struct.RxnArrow = function (params)
{
	params = params || {};
	this.pp = params.pp ? new util.Vec2(params.pp) : new util.Vec2();
};

chem.Struct.RxnArrow.prototype.clone = function ()
{
	return new chem.Struct.RxnArrow(this);
};

chem.Struct.prototype.findConnectedComponent = function (aid) {
    var map = {};
    var list = [aid];
    var ids = util.Set.empty();
    while (list.length > 0) {
        (function() {
            var aid = list.pop();
            map[aid] = 1;
            util.Set.add(ids, aid);
            var atom = this.atoms.get(aid);
            for (var i = 0; i < atom.neighbors.length; ++i) {
                var neiId = this.halfBonds.get(atom.neighbors[i]).end;
                if (!util.Set.contains(ids, neiId))
                    list.push(neiId);
            }
        }).apply(this);
    }
    return ids;
};

chem.Struct.prototype.findConnectedComponents = function (discardExistingFragments) {
	var map = {};
	this.atoms.each(function(aid,atom){
		map[aid] = -1;
	}, this);
	var components = [];
	this.atoms.each(function(aid,atom){
		if ((discardExistingFragments || atom.fragment < 0) && map[aid] < 0) {
            var component = this.findConnectedComponent(aid);
			components.push(component);
            util.Set.each(component, function(aid){
                map[aid] = 1;
            }, this);
		}
	}, this);
	return components;
};

chem.Struct.prototype.markFragment = function (ids) {
    var fid = this.frags.add(new chem.Struct.Fragment());
    util.Set.each(ids, function(aid){
        this.atoms.get(aid).fragment = fid;
    }, this);
};

chem.Struct.prototype.markFragmentByAtomId = function (aid) {
    this.markFragment(this.findConnectedComponent(aid));
};

chem.Struct.prototype.markFragments = function () {
    var components = this.findConnectedComponents();
    for (var i = 0; i < components.length; ++i) {
        this.markFragment(components[i]);
    }
};

chem.Struct.Fragment = function() {
};
chem.Struct.Fragment.prototype.clone = function() {
    return Object.clone(this);
};

chem.Struct.Fragment.getAtoms = function (struct, frid) {
    var atoms = [];
    struct.atoms.each(function(aid, atom) {
        if (atom.fragment == frid)
            atoms.push(aid);
    }, this);
    return atoms;
}

chem.Struct.RGroup = function(logic) {
    logic = logic || {};
    this.frags = new util.Pool();
    this.resth = logic.resth || false;
    this.range = logic.range || '';
    this.ifthen = logic.ifthen || 0;
};

chem.Struct.RGroup.prototype.getAttrs = function() {
    return {
        resth: this.resth,
        range: this.range,
        ifthen: this.ifthen
    };
};

chem.Struct.RGroup.findRGroupByFragment = function(rgroups, frid) {
    var ret;
    rgroups.each(function(rgid, rgroup) {
        if (!Object.isUndefined(rgroup.frags.keyOf(frid))) ret = rgid;
    });
    return ret;
};
chem.Struct.RGroup.prototype.clone = function(fidMap) {
    var ret = new chem.Struct.RGroup(this);
    this.frags.each(function(fnum, fid) {
        ret.frags.add(fidMap ? fidMap[fid] : fid);
    });
    return ret;
};

chem.Struct.prototype.scale = function (scale)
{
    if (scale != 1) {
        this.atoms.each(function(aid, atom){
            atom.pp = atom.pp.scaled(scale);
        }, this);
        this.rxnPluses.each(function(id, item){
            item.pp = item.pp.scaled(scale);
        }, this);
        this.rxnArrows.each(function(id, item){
            item.pp = item.pp.scaled(scale);
        }, this);
        this.sgroups.each(function(id, item){
            item.pp = item.pp ? item.pp.scaled(scale) : null;
        }, this);
    }
};

chem.Struct.prototype.rescale = function ()
{
    var avg = this.getAvgBondLength();
    if (avg < 0 && !this.isReaction) // TODO [MK] this doesn't work well for reactions as the distances between
        // the atoms in different components are generally larger than those between atoms of a single component
        // (KETCHER-341)
        avg = this.getAvgClosestAtomDistance();
    if (avg < 1e-3)
        avg = 1;
    var scale = 1 / avg;
    this.scale(scale);
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.chem || !util.Vec2 || !chem.Struct)
	throw new Error("Vec2 and Molecule should be defined first");

chem.Molfile = function ()
{};

chem.Molfile.loadRGroupFragments = true; // TODO: set to load the fragments

chem.Molfile.parseDecimalInt = function (str)
{
	var val = parseInt(str, 10);

	return isNaN(val) ? 0 : val;
};

chem.Molfile.partitionLine = function (/*string*/ str, /*array of int*/ parts, /*bool*/ withspace)
{
	var res = [];
	for (var i = 0, shift = 0; i < parts.length; ++i)
	{
		res.push(str.slice(shift, shift + parts[i]));
		if (withspace)
			shift++;
		shift += parts[i];
	}
	return res;
};

chem.Molfile.partitionLineFixed = function (/*string*/ str, /*int*/ itemLength, /*bool*/ withspace)
{
	var res = [];
	for (var shift = 0; shift < str.length; shift += itemLength)
	{
		res.push(str.slice(shift, shift + itemLength));
		if (withspace)
			shift++;
	}
	return res;
};

chem.Molfile.parseCTFile = function (molfileLines)
{
    var ret = null;
	if (molfileLines[0].search("\\$RXN") == 0)
		ret = chem.Molfile.parseRxn(molfileLines);
	else
		ret = chem.Molfile.parseMol(molfileLines);
    ret.initHalfBonds();
    ret.initNeighbors();
    ret.markFragments();
    return ret;
};

chem.Molfile.fmtInfo = {
	bondTypeMap: {
		1: chem.Struct.BOND.TYPE.SINGLE,
		2: chem.Struct.BOND.TYPE.DOUBLE,
		3: chem.Struct.BOND.TYPE.TRIPLE,
		4: chem.Struct.BOND.TYPE.AROMATIC,
		5: chem.Struct.BOND.TYPE.SINGLE_OR_DOUBLE,
		6: chem.Struct.BOND.TYPE.SINGLE_OR_AROMATIC,
		7: chem.Struct.BOND.TYPE.DOUBLE_OR_AROMATIC,
		8: chem.Struct.BOND.TYPE.ANY
		},
	bondStereoMap: {
		0: chem.Struct.BOND.STEREO.NONE,
		1: chem.Struct.BOND.STEREO.UP,
		4: chem.Struct.BOND.STEREO.EITHER,
		6: chem.Struct.BOND.STEREO.DOWN,
		3: chem.Struct.BOND.STEREO.CIS_TRANS
		},
	v30bondStereoMap: {
		0: chem.Struct.BOND.STEREO.NONE,
		1: chem.Struct.BOND.STEREO.UP,
		2: chem.Struct.BOND.STEREO.EITHER,
		3: chem.Struct.BOND.STEREO.DOWN
		},
	bondTopologyMap: {
		0: chem.Struct.BOND.TOPOLOGY.EITHER,
		1: chem.Struct.BOND.TOPOLOGY.RING,
		2: chem.Struct.BOND.TOPOLOGY.CHAIN
		},
	countsLinePartition: [3,3,3,3,3,3,3,3,3,3,3,6],
	atomLinePartition: [10,10,10,1,3,2,3,3,3,3,3,3,3,3,3,3,3],
	bondLinePartition: [3,3,3,3,3,3,3],
	atomListHeaderPartition: [3,1,1,4,1,1],
	atomListHeaderLength: 11, // = atomListHeaderPartition.reduce(function(a,b) { return a + b; }, 0)
	atomListHeaderItemLength: 4,
	chargeMap: [0, +3, +2, +1, 0, -1, -2, -3],
	valenceMap: [undefined, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 0],
	implicitHydrogenMap: [undefined, 0, 1, 2, 3, 4],
	v30atomPropMap: {
		'CHG':'charge',
		'RAD':'radical',
		'MASS':'isotope',
		'VAL':'valence',
        'HCOUNT':'hCount',
        'INVRET':'invRet',
        'SUBST':'substitutionCount',
        'UNSAT':'unsaturatedAtom',
        'RBCNT':'ringBondCount'
	},
	rxnItemsPartition: [3,3,3]
};

chem.Molfile.parseAtomLine = function (atomLine)
{
	var mf = chem.Molfile;
	var atomSplit = mf.partitionLine(atomLine, mf.fmtInfo.atomLinePartition);
	var params =
	{
		// generic
		pp: new util.Vec2(parseFloat(atomSplit[0]), -parseFloat(atomSplit[1])),
		label: atomSplit[4].strip(),
		valence: mf.fmtInfo.valenceMap[mf.parseDecimalInt(atomSplit[10])],

		// obsolete
		massDifference: mf.parseDecimalInt(atomSplit[5]),
		charge: mf.fmtInfo.chargeMap[mf.parseDecimalInt(atomSplit[6])],

		// query
		hCount: mf.parseDecimalInt(mf.parseDecimalInt(atomSplit[8])),
		stereoCare: mf.parseDecimalInt(atomSplit[9]) != 0,

		// reaction
		aam: mf.parseDecimalInt(atomSplit[14]),
		invRet: mf.parseDecimalInt(atomSplit[15]),

		// reaction query
		exactChangeFlag: mf.parseDecimalInt(atomSplit[16]) != 0
	};
	params.explicitValence = typeof(params.valence) != 'undefined';
	return new chem.Struct.Atom(params);
};

chem.Molfile.stripV30 = function (line)
{
	if (line.slice(0, 7) != 'M  V30 ')
		throw Error("Prefix invalid");
	return line.slice(7);
};

chem.Molfile.parseAtomLineV3000 = function (line)
{
	var mf = chem.Molfile;
	var split, subsplit, key, value, i;
	split = mf.spaceparsplit(line);
	var params = {
		pp: new util.Vec2(parseFloat(split[2]), -parseFloat(split[3])),
		aam: split[5].strip()
	};
	var label = split[1].strip();
	if (label.charAt(0) == "\"" && label.charAt(label.length - 1) == "\"") {
		label = label.substr(1, label.length - 2); // strip qutation marks
	}
	if (label.charAt(label.length - 1) == "]") { // assume atom list
		label = label.substr(0, label.length - 1); // remove ']'
		var atomListParams = {};
		atomListParams.notList = false;
		if (label.substr(0, 5) == "NOT [") {
			atomListParams.notList = true;
			label = label.substr(5); // remove 'NOT ['
		} else if (label.charAt(0) != "[") {
			throw "Error: atom list expected, found \'" + label + "\'";
		} else {
			label = label.substr(1); // remove '['
		}
		atomListParams.ids = mf.labelsListToIds(label.split(","));
		params['atomList'] = new chem.Struct.AtomList(atomListParams);
		params['label'] = '';
	} else {
		params['label'] = label;
	}
	split.splice(0, 6);
	for (i = 0; i < split.length; ++i) {
		subsplit = mf.splitonce(split[i], '=');
		key = subsplit[0];
		value = subsplit[1];
		if (key in mf.fmtInfo.v30atomPropMap) {
			var ival = mf.parseDecimalInt(value);
			if (key == 'VAL') {
				if (ival == 0)
					continue;
				if (ival == -1)
					ival = 0;
			}
			params[mf.fmtInfo.v30atomPropMap[key]] = ival;
		} else if (key == 'RGROUPS') {
            value = value.strip().substr(1, value.length-2);
            var rgrsplit = value.split(' ').slice(1);
            params.rglabel = 0;
            for (var j = 0; j < rgrsplit.length; ++j) {
                params.rglabel |= 1 << (rgrsplit[j]-1);
            }
        } else if (key == 'ATTCHPT') {
            params.attpnt = value.strip()-0;
        }
	}
	params.explicitValence = typeof(params.valence) != 'undefined';
	return new chem.Struct.Atom(params);
};

chem.Molfile.parseBondLineV3000 = function (line)
{
	var mf = chem.Molfile;
	var split, subsplit, key, value, i;
	split = mf.spaceparsplit(line);
	var params = {
		begin: mf.parseDecimalInt(split[2]) - 1,
		end: mf.parseDecimalInt(split[3]) - 1,
		type: mf.fmtInfo.bondTypeMap[mf.parseDecimalInt(split[1])]
	};
	split.splice(0, 4);
	for (i = 0; i < split.length; ++i) {
		subsplit = mf.splitonce(split[i], '=');
		key = subsplit[0];
		value = subsplit[1];
		if (key == 'CFG')
			params.stereo = mf.fmtInfo.v30bondStereoMap[mf.parseDecimalInt(value)];
		else if (key == 'TOPO')
			params.topology = mf.fmtInfo.bondTopologyMap[mf.parseDecimalInt(value)];
		else if (key == 'RXCTR')
			params.reactingCenterStatus = mf.parseDecimalInt(value);
		else if (key == 'STBOX')
			params.stereoCare = mf.parseDecimalInt(value);
	}
	return new chem.Struct.Bond(params);
};

chem.Molfile.parseBondLine = function (bondLine)
{
	var mf = chem.Molfile;
	var bondSplit = mf.partitionLine(bondLine, mf.fmtInfo.bondLinePartition);
	var params =
	{
		begin: mf.parseDecimalInt(bondSplit[0]) - 1,
		end: mf.parseDecimalInt(bondSplit[1]) - 1,
		type: mf.fmtInfo.bondTypeMap[mf.parseDecimalInt(bondSplit[2])],
		stereo: mf.fmtInfo.bondStereoMap[mf.parseDecimalInt(bondSplit[3])],
		topology: mf.fmtInfo.bondTopologyMap[mf.parseDecimalInt(bondSplit[5])],
		reactingCenterStatus: mf.parseDecimalInt(bondSplit[6])
	};

	return new chem.Struct.Bond(params);
};

chem.Molfile.parseAtomListLine = function (/* string */atomListLine)
{
	var mf = chem.Molfile;
	var split = mf.partitionLine(atomListLine, mf.fmtInfo.atomListHeaderPartition);

	var number = mf.parseDecimalInt(split[0]) - 1;
	var notList = (split[2].strip() == "T");
	var count = mf.parseDecimalInt(split[4].strip());

	var ids = atomListLine.slice(mf.fmtInfo.atomListHeaderLength);
	var list = [];
	var itemLength = mf.fmtInfo.atomListHeaderItemLength;
	for (var i = 0; i < count; ++i)
		list[i] = mf.parseDecimalInt(ids.slice(i * itemLength, (i + 1) * itemLength - 1));

	return {
		"aid": number,
		"atomList" : new chem.Struct.AtomList({
			"notList": notList,
			"ids": list
		})
		};
};

chem.Molfile.readKeyValuePairs = function (str, /* bool */ valueString)
{
	var mf = chem.Molfile;
	var ret = {};
	var partition = mf.partitionLineFixed(str, 3, true);
	var count = mf.parseDecimalInt(partition[0]);
	for (var i = 0; i < count; ++i)
		ret[mf.parseDecimalInt(partition[2 * i + 1]) - 1] =
		valueString ? partition[2 * i + 2].strip() :
		mf.parseDecimalInt(partition[2 * i + 2]);
	return ret;
};

chem.Molfile.readKeyMultiValuePairs = function (str, /* bool */ valueString)
{
	var mf = chem.Molfile;
	var ret = [];
	var partition = mf.partitionLineFixed(str, 3, true);
	var count = mf.parseDecimalInt(partition[0]);
	for (var i = 0; i < count; ++i)
        ret.push([
            mf.parseDecimalInt(partition[2 * i + 1]) - 1,
            valueString ? partition[2 * i + 2].strip() : mf.parseDecimalInt(partition[2 * i + 2])
        ]);
	return ret;
};

chem.Molfile.labelsListToIds = function (labels)
{
	var ids = [];
	for (var i = 0; i < labels.length; ++i) {
		ids.push(chem.Element.getElementByLabel(labels[i].strip()));
	}
	return ids;
};

chem.Molfile.parsePropertyLineAtomList = function (hdr, lst)
{
	var mf = chem.Molfile;
	var aid = mf.parseDecimalInt(hdr[1]) - 1;
	var count = mf.parseDecimalInt(hdr[2]);
	var notList = hdr[4].strip() == 'T';
	var ids = mf.labelsListToIds(lst.slice(0, count));
	var ret = {};
	ret[aid] = new chem.Struct.AtomList({
		"notList": notList,
		"ids": ids
	});
	return ret;
};

chem.Molfile.initSGroup = function (sGroups, propData)
{
	var mf = chem.Molfile;
	var kv = mf.readKeyValuePairs(propData, true);
	for (var key in kv) {
		var type = kv[key];
		if (!(type in chem.SGroup.TYPES))
			throw new Error('Unsupported S-group type');
		var sg = new chem.SGroup(type);
		sg.number = key;
		sGroups[key] = sg;
	}
};

chem.Molfile.applySGroupProp = function (sGroups, propName, propData, numeric)
{
	var mf = chem.Molfile;
	var kv = mf.readKeyValuePairs(propData, !(numeric));
	for (var key in kv) {
		sGroups[key].data[propName] = kv[key];
	}
};

chem.Molfile.toIntArray = function (strArray)
{
	var mf = chem.Molfile;
	var ret = [];
	for (var j = 0; j < strArray.length; ++j)
		ret[j] = mf.parseDecimalInt(strArray[j]);
	return ret;
};

chem.Molfile.applySGroupArrayProp = function (sGroups, propName, propData, shift)
{
	var mf = chem.Molfile;
	var sid = mf.parseDecimalInt(propData.slice(1, 4))-1;
	var num = mf.parseDecimalInt(propData.slice(4, 8));
	var part = mf.toIntArray(mf.partitionLineFixed(propData.slice(8), 3, true));

	if (part.length != num)
		throw new Error('File format invalid');
	if (shift) {
		util.apply(part, function(v) {
			return v + shift;
		});
	}
	sGroups[sid][propName] = sGroups[sid][propName].concat(part);
};

chem.Molfile.applyDataSGroupName = function (sg, name) {
	sg.data.fieldName = name;
};

chem.Molfile.applyDataSGroupQuery = function (sg, query) {
	sg.data.query = query;
};

chem.Molfile.applyDataSGroupQueryOp = function (sg, queryOp) {
	sg.data.queryOp = queryOp;
};

chem.Molfile.applyDataSGroupDesc = function (sGroups, propData) {
	var mf = chem.Molfile;
	var split = mf.partitionLine(propData, [4,31,2,20,2,3], false);
	var id = mf.parseDecimalInt(split[0])-1;
	var fieldName = split[1].strip();
	var fieldType = split[2].strip();
	var units = split[3].strip();
	var query = split[4].strip();
	var queryOp = split[5].strip();
	var sGroup = sGroups[id];
	sGroup.data.fieldType = fieldType;
	sGroup.data.fieldName = fieldName;
	sGroup.data.units = units;
	sGroup.data.query = query;
	sGroup.data.queryOp = queryOp;
};

chem.Molfile.applyDataSGroupInfo = function (sg, propData) {
	var mf = chem.Molfile;
	var split = mf.partitionLine(propData, [10/*x.x*/,10/*y.y*/,4/* eee*/,1/*f*/,1/*g*/,1/*h*/,3/* i */,3/*jjj*/,3/*kkk*/,3/*ll*/,2/*m*/,3/*n*/,2/*oo*/], false);

	var x = parseFloat(split[0]);
	var y = parseFloat(split[1]);
	var attached = split[3].strip() == 'A';
	var absolute = split[4].strip() == 'A';
	var showUnits = split[5].strip() == 'U';
	var nCharsToDisplay = split[7].strip();
	nCharsToDisplay = nCharsToDisplay == 'ALL' ? -1 : mf.parseDecimalInt(nCharsToDisplay);
	var tagChar = split[10].strip();
	var daspPos = mf.parseDecimalInt(split[11].strip());

	sg.pp = new util.Vec2(x, -y);
	sg.data.attached = attached;
	sg.data.absolute = absolute;
	sg.data.showUnits = showUnits;
	sg.data.nCharsToDisplay = nCharsToDisplay;
	sg.data.tagChar = tagChar;
	sg.data.daspPos = daspPos;
};

chem.Molfile.applyDataSGroupInfoLine = function (sGroups, propData) {
	var mf = chem.Molfile;
	var id = mf.parseDecimalInt(propData.substr(0,4))-1;
	var sg = sGroups[id];
	mf.applyDataSGroupInfo(sg, propData.substr(5));
};

chem.Molfile.applyDataSGroupData = function (sg, data, finalize) {
	sg.data.fieldValue = (sg.data.fieldValue || '') + data;
	if (finalize) {
		sg.data.fieldValue = util.stripRight(sg.data.fieldValue);
                if (sg.data.fieldValue.startsWith('"') && sg.data.fieldValue.endsWith('"'))
                    sg.data.fieldValue = sg.data.fieldValue.substr(1, sg.data.fieldValue.length - 2);
        }
};

chem.Molfile.applyDataSGroupDataLine = function (sGroups, propData, finalize) {
	var mf = chem.Molfile;
	var id = mf.parseDecimalInt(propData.substr(0,5))-1;
	var data = propData.substr(5);
	var sg = sGroups[id];
	mf.applyDataSGroupData(sg, data, finalize);
};

chem.Molfile.parsePropertyLines = function (ctab, ctabLines, shift, end, sGroups, rLogic)
{
	var mf = chem.Molfile;
	var props = new util.Map();
	while (shift < end)
	{
		var line = ctabLines[shift];
        if (line.charAt(0) == 'A') {
            if (!props.get('label'))
                props.set('label', new util.Map());
            props.get('label').set(mf.parseDecimalInt(line.slice(3, 6))-1, ctabLines[++shift]);
        } else if (line.charAt(0) == 'M') {
			var type = line.slice(3, 6);
			var propertyData = line.slice(6);
			if (type == "END") {
				break;
			} else if (type == "CHG") {
				if (!props.get('charge'))
					props.set('charge', new util.Map());
				props.get('charge').update(mf.readKeyValuePairs(propertyData));
			} else if (type == "RAD") {
				if (!props.get('radical'))
					props.set('radical', new util.Map());
				props.get('radical').update(mf.readKeyValuePairs(propertyData));
			} else if (type == "ISO") {
				if (!props.get('isotope'))
					props.set('isotope', new util.Map());
				props.get('isotope').update(mf.readKeyValuePairs(propertyData));
			} else if (type == "RBC") {
				if (!props.get('ringBondCount'))
					props.set('ringBondCount', new util.Map());
				props.get('ringBondCount').update(mf.readKeyValuePairs(propertyData));
			} else if (type == "SUB") {
				if (!props.get('substitutionCount'))
					props.set('substitutionCount', new util.Map());
				props.get('substitutionCount').update(mf.readKeyValuePairs(propertyData));
			} else if (type == "UNS") {
				if (!props.get('unsaturatedAtom'))
					props.set('unsaturatedAtom', new util.Map());
				props.get('unsaturatedAtom').update(mf.readKeyValuePairs(propertyData));
			// else if (type == "LIN") // link atom
			} else if (type == "RGP") { // rgroup atom
                if (!props.get('rglabel'))
                    props.set('rglabel', new util.Map());
                var rglabels = props.get('rglabel');
                var a2rs = mf.readKeyMultiValuePairs(propertyData);
                for (var a2ri = 0; a2ri < a2rs.length; a2ri++) {
                    var a2r = a2rs[a2ri];
                    rglabels.set(a2r[0], (rglabels.get(a2r[0]) || 0) | (1 << (a2r[1] - 1)));
                }
			} else if (type == "LOG") { // rgroup atom
                propertyData = propertyData.slice(4);
                var rgid = mf.parseDecimalInt(propertyData.slice(0,3).strip());
                var iii = mf.parseDecimalInt(propertyData.slice(4,7).strip());
                var hhh = mf.parseDecimalInt(propertyData.slice(8,11).strip());
                var ooo = propertyData.slice(12).strip();
                var logic = {};
                if (iii > 0)
                    logic.ifthen = iii;
                logic.resth = hhh == 1;
                logic.range = ooo;
                rLogic[rgid] = logic;
            } else if (type == "APO") {
                if (!props.get('attpnt'))
                    props.set('attpnt', new util.Map());
                props.get('attpnt').update(mf.readKeyValuePairs(propertyData));
            } else if (type == "ALS") { // atom list
				if (!props.get('atomList'))
					props.set('atomList', new util.Map());
				props.get('atomList').update(
					mf.parsePropertyLineAtomList(
						mf.partitionLine(propertyData, [1,3,3,1,1,1]),
						mf.partitionLineFixed(propertyData.slice(10), 4, false)));
			} else if (type == "STY") { // introduce s-group
				mf.initSGroup(sGroups, propertyData);
			} else if (type == "SST") {
				mf.applySGroupProp(sGroups, 'subtype', propertyData);
			} else if (type == "SLB") {
				mf.applySGroupProp(sGroups, 'label', propertyData, true);
			} else if (type == "SCN") {
				mf.applySGroupProp(sGroups, 'connectivity', propertyData);
			} else if (type == "SAL") {
				mf.applySGroupArrayProp(sGroups, 'atoms', propertyData, -1);
			} else if (type == "SBL") {
				mf.applySGroupArrayProp(sGroups, 'bonds', propertyData, -1);
			} else if (type == "SPA") {
				mf.applySGroupArrayProp(sGroups, 'patoms', propertyData, -1);
			} else if (type == "SMT") {
				var sid = mf.parseDecimalInt(propertyData.slice(0, 4))-1;
				sGroups[sid].data.subscript = propertyData.slice(4).strip();
			} else if (type == "SDT") {
				mf.applyDataSGroupDesc(sGroups, propertyData);
			} else if (type == "SDD") {
				mf.applyDataSGroupInfoLine(sGroups, propertyData);
			} else if (type == "SCD") {
				mf.applyDataSGroupDataLine(sGroups, propertyData, false);
			} else if (type == "SED") {
				mf.applyDataSGroupDataLine(sGroups, propertyData, true);
			}
		}
		++shift;
	}
	return props;
};

chem.Molfile.applyAtomProp = function (atoms /* Pool */, values /* util.Map */, propId /* string */, clean /* boolean */)
{
	values.each(function(aid, propVal){
		atoms.get(aid)[propId] = propVal;
	});
};

chem.Molfile.parseCTabV2000 = function (ctabLines, countsSplit)
{
	var ctab = new chem.Struct();
	var i;
	var mf = chem.Molfile;
	var atomCount = mf.parseDecimalInt(countsSplit[0]);
	var bondCount = mf.parseDecimalInt(countsSplit[1]);
	var atomListCount = mf.parseDecimalInt(countsSplit[2]);
	ctab.isChiral = mf.parseDecimalInt(countsSplit[4]) != 0;
	var stextLinesCount = mf.parseDecimalInt(countsSplit[5]);
	var propertyLinesCount = mf.parseDecimalInt(countsSplit[10]);

	var shift = 0;
	var atomLines = ctabLines.slice(shift, shift + atomCount);
	shift += atomCount;
	var bondLines = ctabLines.slice(shift, shift + bondCount);
	shift += bondCount;
	var atomListLines = ctabLines.slice(shift, shift + atomListCount);
	shift += atomListCount + stextLinesCount;

	var atoms = atomLines.map(mf.parseAtomLine);
	for (i = 0; i < atoms.length; ++i)
		ctab.atoms.add(atoms[i]);
	var bonds = bondLines.map(mf.parseBondLine);
	for (i = 0; i < bonds.length; ++i)
		ctab.bonds.add(bonds[i]);

	var atomLists = atomListLines.map(mf.parseAtomListLine);
	atomLists.each(function(pair){
		ctab.atoms.get(pair.aid).atomList = pair.atomList;
		ctab.atoms.get(pair.aid).label = '';
	});

	var sGroups = {}, rLogic = {};
	var props = mf.parsePropertyLines(ctab, ctabLines, shift,
		Math.min(ctabLines.length, shift + propertyLinesCount), sGroups, rLogic);
	props.each(function (propId, values) {
		mf.applyAtomProp(ctab.atoms, values, propId);
	});

	var atomMap = {};
    var sid;
	for (sid in sGroups) {
		chem.SGroup.addGroup(ctab, sGroups[sid], atomMap);
	}
	var emptyGroups = [];
	for (sid in sGroups) {
		chem.SGroup.filter(ctab, sGroups[sid], atomMap);
		if (sGroups[sid].atoms.length == 0 && !sGroups[sid].allAtoms)
			emptyGroups.push(sid);
	}
	for (i = 0; i < emptyGroups.length; ++i) {
		ctab.sgroups.remove(emptyGroups[i]);
	}
        for (var rgid in rLogic) {
            ctab.rgroups.set(rgid, new chem.Struct.RGroup(rLogic[rgid]));
        }
	return ctab;
};

// split a line by spaces outside parentheses
chem.Molfile.spaceparsplit = function (line)
{
	var split = [], pc = 0, c, i, i0 = -1;
	var line_array = line.toArray(); // IE7 doesn't support line[i]
	var quoted = false;

	for (i = 0; i < line.length; ++i)
	{
		c = line_array[i];
		if (c == '(')
			pc++;
		else if (c == ')')
			pc--;
		if (c == '"')
			quoted = !quoted;
		if (!quoted && line_array[i] == ' ' && pc == 0) {
			if (i > i0 + 1)
				split.push(line.slice(i0+1, i));
			i0 = i;
		}
	}
	if (i > i0 + 1)
		split.push(line.slice(i0 + 1, i));
	i0 = i;
	return split;
};

chem.Molfile.splitonce = function (line, delim)
{
	var p = line.indexOf(delim);
	return [line.slice(0,p),line.slice(p+1)];
};

chem.Molfile.splitSGroupDef = function (line)
{
	var split = [];
	var braceBalance = 0;
	var quoted = false;
	for (var i = 0; i < line.length; ++i) {
		var c = line.charAt(i);
		if (c == '"') {
			quoted = !quoted;
		} else if (!quoted) {
			if (c == '(') {
				braceBalance++;
			} else if (c == ')') {
				braceBalance--;
			} else if (c == ' ' && braceBalance == 0) {
				split.push(line.slice(0, i));
				line = line.slice(i+1).strip();
				i = 0;
			}
		}
	}
	if (braceBalance != 0)
		throw "Brace balance broken. S-group properies invalid!";
	if (line.length > 0)
		split.push(line.strip());
	return split;
};

chem.Molfile.parseBracedNumberList = function (line, shift)
{
	if (!line)
		return null;
	var list = [];
	line = line.strip();
	line = line.substr(1, line.length-2);
	var split = line.split(" ");
	shift = shift || 0;
	for (var i = 1; i < split.length; ++i) { // skip the first element
		list.push(split[i] - 0 + shift);
	}
	return list;
};

chem.Molfile.v3000parseCollection = function (ctab, ctabLines, shift)
{
    shift++;
    while (ctabLines[shift].strip() != "M  V30 END COLLECTION")
        shift++;
    shift++;
    return shift;
};

chem.Molfile.v3000parseSGroup = function (ctab, ctabLines, sgroups, atomMap, shift)
{
    var mf = chem.Molfile;
    var line = '';
    shift++;
    while (shift < ctabLines.length) {
        line = mf.stripV30(ctabLines[shift++]).strip();
        if (line.strip() == 'END SGROUP')
            return shift;
        while (line[line.length-1] == '-')
            line = (line.substr(0, line.length - 1) +
                mf.stripV30(ctabLines[shift++])).strip();
        var split = mf.splitSGroupDef(line);
        var type = split[1];
        var sg = new chem.SGroup(type);
        sg.number = split[0] - 0;
        sg.type = type;
        sg.label = split[2] - 0;
        sgroups[sg.number] = sg;
        var props = {};
        for (var i = 3; i < split.length; ++i) {
            var subsplit = mf.splitonce(split[i],'=');
            if (subsplit.length != 2) {
                throw "A record of form AAA=BBB or AAA=(...) expected, got '" + split[i] + "'";
            }
            var name = subsplit[0];
            if (!(name in props))
                props[name] = [];
            props[name].push(subsplit[1]);
        }
        sg.atoms = mf.parseBracedNumberList(props['ATOMS'][0], -1);
        if (props['PATOMS'])
            sg.patoms = mf.parseBracedNumberList(props['PATOMS'][0], -1);
        sg.bonds = props['BONDS'] ? mf.parseBracedNumberList(props['BONDS'][0], -1) : [];
        var brkxyzStrs = props['BRKXYZ'];
        sg.brkxyz = [];
        if (brkxyzStrs) {
            for (var j = 0; j < brkxyzStrs.length; ++j)
                sg.brkxyz.push(mf.parseBracedNumberList(brkxyzStrs[j]));
        }
        if (props['MULT']) {
            sg.data.subscript = props['MULT'][0]-0;
        }
        if (props['LABEL']) {
            sg.data.subscript = props['LABEL'][0].strip();
        }
        if (props['CONNECT']) {
            sg.data.connectivity = props['CONNECT'][0].toLowerCase();
        }
        if (props['FIELDDISP']) {
            mf.applyDataSGroupInfo(sg, util.stripQuotes(props['FIELDDISP'][0]));
        }
        if (props['FIELDDATA']) {
            mf.applyDataSGroupData(sg, props['FIELDDATA'][0], true);
        }
        if (props['FIELDNAME']) {
            mf.applyDataSGroupName(sg, props['FIELDNAME'][0]);
        }
        if (props['QUERYTYPE']) {
            mf.applyDataSGroupQuery(sg, props['QUERYTYPE'][0]);
        }
        if (props['QUERYOP']) {
            mf.applyDataSGroupQueryOp(sg, props['QUERYOP'][0]);
        }
        chem.SGroup.addGroup(ctab, sg, atomMap);
    }
    throw new Error("S-group declaration incomplete.");
};

chem.Molfile.parseCTabV3000 = function (ctabLines, norgroups)
{
    var ctab = new chem.Struct();
    var mf = chem.Molfile;

    var shift = 0;
    if (ctabLines[shift++].strip() != "M  V30 BEGIN CTAB")
        throw Error("CTAB V3000 invalid");
    if (ctabLines[shift].slice(0, 13) != "M  V30 COUNTS")
        throw Error("CTAB V3000 invalid");
    var vals = ctabLines[shift].slice(14).split(' ');
    ctab.isChiral = (mf.parseDecimalInt(vals[4]) == 1);
    shift++;

    if (ctabLines[shift].strip() == "M  V30 BEGIN ATOM") {
        shift++;
        var line;
        while (shift < ctabLines.length) {
            line = mf.stripV30(ctabLines[shift++]).strip();
            if (line == 'END ATOM')
                break;
            while (line[line.length-1] == '-')
                line = (line.substring(0, line.length - 1) + mf.stripV30(ctabLines[shift++])).strip();
            ctab.atoms.add(mf.parseAtomLineV3000(line));
        }

        if (ctabLines[shift].strip() == "M  V30 BEGIN BOND")
        {
            shift++;
            while (shift < ctabLines.length) {
                line = mf.stripV30(ctabLines[shift++]).strip();
                if (line == 'END BOND')
                    break;
                while (line[line.length - 1] == '-')
                    line = (line.substring(0, line.length - 1) + mf.stripV30(ctabLines[shift++])).strip();
                ctab.bonds.add(mf.parseBondLineV3000(line));
            }
        }

        // TODO: let sections follow in arbitrary order
        var sgroups = {};
        var atomMap = {};

        while (ctabLines[shift].strip() != "M  V30 END CTAB") {
            if (ctabLines[shift].strip() == "M  V30 BEGIN COLLECTION") {
                 // TODO: read collection information
                shift = mf.v3000parseCollection(ctab, ctabLines, shift);
            } else if (ctabLines[shift].strip() == "M  V30 BEGIN SGROUP") {
                shift = mf.v3000parseSGroup(ctab, ctabLines, sgroups, atomMap, shift);
            } else {
                throw Error("CTAB V3000 invalid");
            }
        }
    }
    if (ctabLines[shift++].strip() != "M  V30 END CTAB")
        throw Error("CTAB V3000 invalid");

    if (!norgroups) {
        mf.readRGroups3000(ctab, ctabLines.slice(shift));
    }

    return ctab;
};

chem.Molfile.readRGroups3000 = function (ctab, /* string */ ctabLines) /* chem.Struct */
{
    var rfrags = {};
    var rLogic = {};
    var shift = 0;
    var mf = chem.Molfile;
    while (shift < ctabLines.length && ctabLines[shift].search("M  V30 BEGIN RGROUP") == 0)
    {
        var id = ctabLines[shift++].split(' ').pop();
        rfrags[id] = [];
        rLogic[id] = {};
        while (true) {
            var line = ctabLines[shift].strip();
            if (line.search("M  V30 RLOGIC") == 0) {
                line = line.slice(13);
                var rlsplit = line.strip().split(/\s+/g);
                var iii = mf.parseDecimalInt(rlsplit[0]);
                var hhh = mf.parseDecimalInt(rlsplit[1]);
                var ooo = rlsplit.slice(2).join(" ");
                var logic = {};
                if (iii > 0)
                    logic.ifthen = iii;
                logic.resth = hhh == 1;
                logic.range = ooo;
                rLogic[id] = logic;
                shift++;
                continue;
            }
            if (line != "M  V30 BEGIN CTAB")
                throw Error("CTAB V3000 invalid");
            for (var i = 0; i < ctabLines.length; ++i)
                if (ctabLines[shift+i].strip() == "M  V30 END CTAB")
                    break;
            var lines = ctabLines.slice(shift, shift+i+1);
            var rfrag = this.parseCTabV3000(lines, true);
            rfrags[id].push(rfrag);
            shift = shift + i + 1;
            if (ctabLines[shift].strip() == "M  V30 END RGROUP") {
                shift++;
                break;
            }
        }
    }

    for (var rgid in rfrags) {
        for (var j = 0; j < rfrags[rgid].length; ++j) {
            var rg = rfrags[rgid][j];
            rg.rgroups.set(rgid, new chem.Struct.RGroup(rLogic[rgid]));
            var frid = rg.frags.add(new chem.Struct.Fragment());
            rg.rgroups.get(rgid).frags.add(frid);
            rg.atoms.each(function(aid, atom) {atom.fragment = frid;});
            rg.mergeInto(ctab);
        }
    }
};

chem.Molfile.parseMol = function (/* string */ ctabLines) /* chem.Struct */
{
    if (ctabLines[0].search("\\$MDL") == 0) {
        return this.parseRg2000(ctabLines);
    }
	ctabLines = ctabLines.slice(3);
    return this.parseCTab(ctabLines);
};

chem.Molfile.parseCTab = function (/* string */ ctabLines) /* chem.Struct */
{
	var mf = chem.Molfile;
	var countsSplit = mf.partitionLine(ctabLines[0], mf.fmtInfo.countsLinePartition);
	var version = countsSplit[11].strip();
	ctabLines = ctabLines.slice(1);
	if (version == 'V2000')
		return this.parseCTabV2000(ctabLines, countsSplit);
	else if (version == 'V3000')
		return this.parseCTabV3000(ctabLines, !chem.Molfile.loadRGroupFragments);
	else
		throw Error("Molfile version unknown: " + version);
};

chem.MolfileSaver = function (v3000)
{
	this.molecule = null;
	this.molfile = null;

	this.v3000 = v3000 || false
};

chem.MolfileSaver.prototype.prepareSGroups = function (skipErrors)
{
	var mol = this.molecule;
	var sgroups = mol.sgroups;
	var toRemove = [];
	sgroups.each(function(id, sg) {
		try {
			sg.prepareForSaving(mol);
		} catch (ex) {
			if (skipErrors && typeof(ex.id) == 'number') {
				toRemove.push(ex.id);
			} else {
				throw ex;
			}
		}
	});
        if (toRemove.length > 0) {
            alert("WARNING: " + toRemove.length.toString() + " invalid S-groups were detected. They will be omitted." );
        }
	for (var i = 0; i < toRemove.length; ++i) {
		mol.sGroupDelete(toRemove[i]);
	}
	return mol;
};

chem.MolfileSaver.getComponents = function (molecule) {
	var ccs = molecule.findConnectedComponents(true);
	var submols = [];
	var barriers = [];
	var arrowPos = null;
	molecule.rxnArrows.each(function(id, item){ // there's just one arrow
		arrowPos = item.pp.x;
	});
	molecule.rxnPluses.each(function(id, item){
		barriers.push(item.pp.x);
	});
	if (arrowPos != null)
		barriers.push(arrowPos);
	barriers.sort(function(a,b) {return a - b;});
	var components = [];

	var i;
	for (i = 0; i < ccs.length; ++i) {
		var bb = molecule.getCoordBoundingBox(ccs[i]);
		var c = util.Vec2.lc2(bb.min, 0.5, bb.max, 0.5);
		var j = 0;
		while (c.x > barriers[j])
			++j;
		components[j] = components[j] || {};
		util.Set.mergeIn(components[j], ccs[i]);
	}
	var submolTexts = [];
	var reactants = [], products = [];
	for (i = 0; i < components.length; ++i) {
		if (!components[i]) {
			submolTexts.push("");
			continue;
		}
		bb = molecule.getCoordBoundingBox(components[i]);
		c = util.Vec2.lc2(bb.min, 0.5, bb.max, 0.5);
		if (c.x < arrowPos)
			reactants.push(components[i]);
		else
			products.push(components[i]);
	}

	return {
		'reactants':reactants,
		'products':products
	};
};

chem.MolfileSaver.prototype.getCTab = function (molecule, rgroups)
{
	this.molecule = molecule.clone();
	this.molfile = '';
    this.writeCTab2000(rgroups);
    return this.molfile;
};

chem.MolfileSaver.prototype.saveMolecule = function (molecule, skipSGroupErrors, norgroups)
{
	this.reaction = molecule.rxnArrows.count() > 0;
	if (molecule.rxnArrows.count() > 1)
		throw new Error("Reaction may not contain more than one arrow");
	this.molfile = '';
	if (this.reaction) {
        if (molecule.rgroups.count() > 0)
            alert("Reactions with r-groups are not supported at the moment. R-fragments will be discarded in saving");
		var components = chem.MolfileSaver.getComponents(molecule);

		var reactants = components.reactants, products = components.products, all = reactants.concat(products);
		this.molfile = "$RXN\n\n\n\n" + util.paddedInt(reactants.length, 3) + util.paddedInt(products.length, 3) + util.paddedInt(0, 3) + "\n";
		for (var i = 0; i < all.length; ++i) {
			var saver = new chem.MolfileSaver(false);
			var submol = molecule.clone(all[i], null, true);
			var molfile = saver.saveMolecule(submol, false, true);
			this.molfile += "$MOL\n" + molfile;
		}
		return this.molfile;
	}

    if (molecule.rgroups.count() > 0) {
        if (norgroups) {
            molecule = molecule.getScaffold();
        } else {
            var scaffold = new chem.MolfileSaver(false).getCTab(molecule.getScaffold(), molecule.rgroups);
            this.molfile = "$MDL  REV  1\n$MOL\n$HDR\n\n\n\n$END HDR\n";
            this.molfile += "$CTAB\n" + scaffold + "$END CTAB\n";

            molecule.rgroups.each(function(rgid, rg){
                this.molfile += "$RGP\n";
                this.writePaddedNumber(rgid, 3);
                this.molfile += "\n";
                rg.frags.each(function(fnum, fid) {
                    var group = new chem.MolfileSaver(false).getCTab(molecule.getFragment(fid));
                    this.molfile += "$CTAB\n" + group + "$END CTAB\n";
                }, this);
                this.molfile += "$END RGP\n";
            }, this);
            this.molfile += "$END MOL\n";

            return this.molfile;
        }
    }

	this.molecule = molecule.clone();

	this.prepareSGroups(skipSGroupErrors);

	this.writeHeader();

	// TODO: saving to V3000
	this.writeCTab2000();

	return this.molfile;
};

chem.MolfileSaver.prototype.writeHeader = function ()
{
	var date = new Date();

	this.writeCR();
	this.writeWhiteSpace(2);
	this.write('Ketcher');
	this.writeWhiteSpace();
	this.writeCR((date.getMonth() + 1).toPaddedString(2) + date.getDate().toPaddedString(2) + (date.getFullYear() % 100).toPaddedString(2) +
		date.getHours().toPaddedString(2) + date.getMinutes().toPaddedString(2) + '2D 1   1.00000     0.00000     0');
	this.writeCR();
};

chem.MolfileSaver.prototype.write = function (str)
{
	this.molfile += str;
};

chem.MolfileSaver.prototype.writeCR = function (str)
{
	if (arguments.length == 0)
		str = '';

	this.molfile += str + '\n';
};

chem.MolfileSaver.prototype.writeWhiteSpace = function (length)
{
	if (arguments.length == 0)
		length = 1;

	length.times(function ()
	{
		this.write(' ');
	}, this);
};

chem.MolfileSaver.prototype.writePadded = function (str, width)
{
	this.write(str);
	this.writeWhiteSpace(width - str.length);
};

chem.MolfileSaver.prototype.writePaddedNumber = function (number, width)
{
	var str = (number - 0).toString();

	this.writeWhiteSpace(width - str.length);
	this.write(str);
};

chem.MolfileSaver.prototype.writePaddedFloat = function (number, width, precision)
{
	this.write(util.paddedFloat(number, width, precision));
};

chem.MolfileSaver.prototype.writeCTab2000Header = function ()
{
	this.writePaddedNumber(this.molecule.atoms.count(), 3);
	this.writePaddedNumber(this.molecule.bonds.count(), 3);

	this.writePaddedNumber(0, 3);
	this.writeWhiteSpace(3);
	this.writePaddedNumber(this.molecule.isChiral ? 1 : 0, 3);
	this.writePaddedNumber(0, 3);
	this.writeWhiteSpace(12);
	this.writePaddedNumber(999, 3);
	this.writeCR(' V2000');
};

chem.MolfileSaver.prototype.writeCTab2000 = function (rgroups)
{
	this.writeCTab2000Header();

	this.mapping = {};
	var i = 1;

	var atomList_list = [];
    var atomLabel_list = [];
	this.molecule.atoms.each(function (id, atom)
	{
		this.writePaddedFloat(atom.pp.x, 10, 4);
		this.writePaddedFloat(-atom.pp.y, 10, 4);
		this.writePaddedFloat(0, 10, 4);
		this.writeWhiteSpace();

		var label = atom.label;
		if (atom.atomList != null) {
			label = 'L';
			atomList_list.push(id);
		} else if (chem.Element.getElementByLabel(label) == null && ['A', 'Q', 'X', '*', 'R#'].indexOf(label) == -1) {
            label = 'C';
            atomLabel_list.push(id);
        }
		this.writePadded(label, 3);
		this.writePaddedNumber(0, 2);
		this.writePaddedNumber(0, 3);
		this.writePaddedNumber(0, 3);

		if (Object.isUndefined(atom.hCount))
			atom.hCount = 0;
		this.writePaddedNumber(atom.hCount, 3);

		if (Object.isUndefined(atom.stereoCare))
			atom.stereoCare = 0;
		this.writePaddedNumber(atom.stereoCare, 3);

		this.writePaddedNumber(!atom.explicitValence ? 0 : (atom.valence == 0 ? 15 : atom.valence), 3);

        this.writePaddedNumber(0, 3);
        this.writePaddedNumber(0, 3);
        this.writePaddedNumber(0, 3);

		if (Object.isUndefined(atom.aam))
			atom.aam = 0;
		this.writePaddedNumber(atom.aam, 3);

		if (Object.isUndefined(atom.invRet))
			atom.invRet = 0;
		this.writePaddedNumber(atom.invRet, 3);

		if (Object.isUndefined(atom.exactChangeFlag))
			atom.exactChangeFlag = 0;
		this.writePaddedNumber(atom.exactChangeFlag, 3);

		this.writeCR();

		this.mapping[id] = i;
		i++;
	}, this);

	this.bondMapping = {};
	i = 1;
	this.molecule.bonds.each(function (id, bond)
	{
		this.bondMapping[id] = i++;
		this.writePaddedNumber(this.mapping[bond.begin], 3);
		this.writePaddedNumber(this.mapping[bond.end], 3);
		this.writePaddedNumber(bond.type, 3);

		if (Object.isUndefined(bond.stereo))
			bond.stereo = 0;
		this.writePaddedNumber(bond.stereo, 3);

		this.writeWhiteSpace(3);

		if (Object.isUndefined(bond.topology))
			bond.topology = 0;
		this.writePaddedNumber(bond.topology, 3);

		if (Object.isUndefined(bond.reactingCenterStatus))
			bond.reactingCenterStatus = 0;
		this.writePaddedNumber(bond.reactingCenterStatus, 3);

		this.writeCR();
	}, this);

    while (atomLabel_list.length > 0) {
        this.write('A  ');this.writePaddedNumber(atomLabel_list[0] + 1, 3);this.writeCR();
        this.writeCR(this.molecule.atoms.get(atomLabel_list[0]).label);
        atomLabel_list.splice(0, 1);
    }

    var charge_list = new Array();
    var isotope_list = new Array();
    var radical_list = new Array();
    var rglabel_list = new Array();
    var rglogic_list = new Array();
    var aplabel_list = new Array();
    var rbcount_list = new Array();
    var unsaturated_list = new Array();
    var substcount_list = new Array();

	this.molecule.atoms.each(function (id, atom)
	{
        if (atom.charge != 0)
            charge_list.push([id, atom.charge]);
        if (atom.isotope != 0)
            isotope_list.push([id, atom.isotope]);
        if (atom.radical != 0)
            radical_list.push([id, atom.radical]);
        if (atom.rglabel != null && atom.label == 'R#') { // TODO need to force rglabel=null when label is not 'R#'
            for (var rgi = 0; rgi < 32; rgi++) {
                if (atom.rglabel & (1 << rgi)) rglabel_list.push([id, rgi + 1]);
            }
        }
        if (atom.attpnt != null)
            aplabel_list.push([id, atom.attpnt]);
        if (atom.ringBondCount != 0)
            rbcount_list.push([id, atom.ringBondCount]);
        if (atom.substitutionCount != 0)
            substcount_list.push([id, atom.substitutionCount]);
        if (atom.unsaturatedAtom != 0)
            unsaturated_list.push([id, atom.unsaturatedAtom]);
	});

    if (rgroups)
        rgroups.each(function (rgid, rg) {
            if (rg.resth || rg.ifthen > 0 || rg.range.length > 0) {
                var line = '  1 ' + util.paddedInt(rgid, 3) + ' ' + util.paddedInt(rg.ifthen, 3) + ' ' + util.paddedInt(rg.resth ? 1 : 0, 3) + ' ' + rg.range;
                rglogic_list.push(line);
            }
        });

    var writeAtomPropList = function (prop_id, values)
    {
        while (values.length > 0)
        {
            var part = new Array();

            while (values.length > 0 && part.length < 8)
            {
                part.push(values[0]);
                values.splice(0, 1);
            }

            this.write(prop_id);
            this.writePaddedNumber(part.length, 3);

            part.each(function (value)
            {
                this.writeWhiteSpace();
                this.writePaddedNumber(this.mapping[value[0]], 3);
                this.writeWhiteSpace();
                this.writePaddedNumber(value[1], 3);
            }, this);

            this.writeCR();
        }
    };

    writeAtomPropList.call(this, 'M  CHG', charge_list);
    writeAtomPropList.call(this, 'M  ISO', isotope_list);
    writeAtomPropList.call(this, 'M  RAD', radical_list);
    writeAtomPropList.call(this, 'M  RGP', rglabel_list);
    for (var j = 0; j < rglogic_list.length; ++j) {
        this.write('M  LOG' + rglogic_list[j] + '\n');
    }
    writeAtomPropList.call(this, 'M  APO', aplabel_list);
    writeAtomPropList.call(this, 'M  RBC', rbcount_list);
    writeAtomPropList.call(this, 'M  SUB', substcount_list);
    writeAtomPropList.call(this, 'M  UNS', unsaturated_list);

	if (atomList_list.length > 0)
	{
		for (j = 0; j < atomList_list.length; ++j) {
			var aid = atomList_list[j];
			var atomList = this.molecule.atoms.get(aid).atomList;
			this.write('M  ALS');
			this.writePaddedNumber(aid+1, 4);
			this.writePaddedNumber(atomList.ids.length, 3);
			this.writeWhiteSpace();
			this.write(atomList.notList ? 'T' : 'F');

			var labelList = atomList.labelList();
			for (var k = 0; k < labelList.length; ++k) {
				this.writeWhiteSpace();
				this.writePadded(labelList[k], 3);
			}
			this.writeCR();
		}
	}

	var sgmap = {}, cnt = 1;
	this.molecule.sgroups.each(function (id) {
		sgmap[id] = cnt++;
	});
	if (cnt > 1) {
		this.write('M  STY');
		this.writePaddedNumber(cnt - 1, 3);
		this.molecule.sgroups.each(function (id, sgroup) {
			this.writeWhiteSpace(1);
			this.writePaddedNumber(sgmap[id], 3);
			this.writeWhiteSpace(1);
			this.writePadded(sgroup.type, 3);
		}, this);
		this.writeCR();

		// TODO: write subtype, M SST

		this.write('M  SLB');
		this.writePaddedNumber(cnt - 1, 3);
		this.molecule.sgroups.each(function (id, sgroup) {
			this.writeWhiteSpace(1);
			this.writePaddedNumber(sgmap[id], 3);
			this.writeWhiteSpace(1);
			this.writePaddedNumber(sgmap[id], 3);
		}, this);
		this.writeCR();

		// connectivity
		var connectivity = '';
		var connectivityCnt = 0;
		this.molecule.sgroups.each(function (id, sgroup) {
			if (sgroup.type == 'SRU' && sgroup.data.connectivity) {
				connectivity += ' ';
				connectivity += util.stringPadded(sgmap[id].toString(), 3);
				connectivity += ' ';
				connectivity += util.stringPadded(sgroup.data.connectivity, 3, true);
				connectivityCnt++;
			}
		}, this);
		if (connectivityCnt > 0) {
			this.write('M  SCN');
			this.writePaddedNumber(connectivityCnt, 3);
			this.write(connectivity.toUpperCase());
			this.writeCR();
		}

		this.molecule.sgroups.each(function (id, sgroup) {
			if (sgroup.type == 'SRU') {
				this.write('M  SMT ');
				this.writePaddedNumber(sgmap[id], 3);
				this.writeWhiteSpace();
				this.write(sgroup.data.subscript || 'n');
				this.writeCR();
			}
		}, this);

		this.molecule.sgroups.each(function (id, sgroup) {
			this.writeCR(sgroup.saveToMolfile(this.molecule, sgmap, this.mapping, this.bondMapping));
		}, this);
	}

	// TODO: write M  APO
	// TODO: write M  AAL
	// TODO: write M  RGP
	// TODO: write M  LOG

	this.writeCR('M  END');
};

chem.Molfile.parseRxn = function (/* string[] */ ctabLines) /* chem.Struct */
{
	var mf = chem.Molfile;
	var split = ctabLines[0].strip().split(' ');
	if (split.length > 1 && split[1] == 'V3000')
		return mf.parseRxn3000(ctabLines);
	else
		return mf.parseRxn2000(ctabLines);
};

chem.Molfile.parseRxn2000 = function (/* string[] */ ctabLines) /* chem.Struct */
{
	var mf = chem.Molfile;
	ctabLines = ctabLines.slice(4);
	var countsSplit = mf.partitionLine(ctabLines[0], mf.fmtInfo.rxnItemsPartition);
	var nReactants = countsSplit[0]-0,
	nProducts = countsSplit[1]-0,
	nAgents = countsSplit[2]-0;
	ctabLines = ctabLines.slice(2); // consume counts line and following $MOL

	var ret = new chem.Struct();
	var molLines = [];
	var i0 = 0, i;
	for (i = 0; i < ctabLines.length; ++i) {
		if (ctabLines[i].substr(0, 4) == "$MOL") {
			if (i > i0)
				molLines.push(ctabLines.slice(i0, i));
			i0 = i + 1;
		}
	}
	molLines.push(ctabLines.slice(i0));
	var mols = [];
	for (var j = 0; j < molLines.length; ++j) {
		var mol = chem.Molfile.parseMol(molLines[j]);
		mols.push(mol);
	}
	return mf.rxnMerge(mols, nReactants, nProducts, nAgents);
};

chem.Molfile.parseRxn3000 = function (/* string[] */ ctabLines) /* chem.Struct */
{
	var mf = chem.Molfile;
	ctabLines = ctabLines.slice(4);
	var countsSplit = ctabLines[0].split(/\s+/g).slice(3);
	var nReactants = countsSplit[0]-0,
	nProducts = countsSplit[1]-0,
	nAgents = countsSplit.length > 2 ? countsSplit[2]-0 : 0;

    var assert = function (condition) {
        if (!condition)
            throw new Error("CTab format invalid");
    };

    var findCtabEnd = function (i) {
        for (var j = i; j < ctabLines.length; ++j) {
            if (ctabLines[j].strip() == "M  V30 END CTAB")
                return j;
        }
        assert(false);
    };

    var findRGroupEnd = function (i) {
        for (var j = i; j < ctabLines.length; ++j)
            if (ctabLines[j].strip() == "M  V30 END RGROUP")
                return j;
        assert(false);
    };

	var molLinesReactants = [], molLinesProducts = [], current = null, rGroups = [];
	for (var i = 0; i < ctabLines.length; ++i) {
		var line = ctabLines[i].strip();

        if (line.startsWith("M  V30 COUNTS")) {
            // do nothing
        } else if (line == "M  END") {
            break; // stop reading
        } else if (line == "M  V30 BEGIN PRODUCT") {
            assert(current == null);
			current = molLinesProducts;
        } else if (line == "M  V30 END PRODUCT") {
            assert(current === molLinesProducts);
            current = null;
		} else if (line == "M  V30 BEGIN REACTANT") {
            assert(current == null);
			current = molLinesReactants;
        } else if (line == "M  V30 END REACTANT") {
            assert(current === molLinesReactants);
            current = null;
        } else if (line.startsWith("M  V30 BEGIN RGROUP")) {
            assert(current == null);
            var j = findRGroupEnd(i);
            rGroups.push(ctabLines.slice(i,j+1));
            i = j;
		} else if (line == "M  V30 BEGIN CTAB") {
            var j = findCtabEnd(i);
            current.push(ctabLines.slice(i,j+1));
            i = j;
		} else {
            throw new Error("line unrecognized: " + line);
        }
	}
	var mols = [];
	var molLines = molLinesReactants.concat(molLinesProducts);
	for (var j = 0; j < molLines.length; ++j) {
		var mol = chem.Molfile.parseCTabV3000(molLines[j], countsSplit);
		mols.push(mol);
	}
	var ctab = mf.rxnMerge(mols, nReactants, nProducts, nAgents);

    mf.readRGroups3000(ctab, function (array) {
        var res = [];
        for (var k = 0; k < array.length; ++k) {
            res = res.concat(array[k]);
        }
        return res;
    }(rGroups));

    return ctab;
};

chem.Molfile.rxnMerge = function (mols, nReactants, nProducts, nAgents) /* chem.Struct */
{
	var mf = chem.Molfile;

	var ret = new chem.Struct();
	var bbReact = [],
		bbAgent = [],
		bbProd = [];
	var molReact = [],
		molAgent = [],
		molProd = [];
        var j;
        var bondLengthData = {cnt:0,totalLength:0};
	for (j = 0; j < mols.length; ++j) {
            var mol = mols[j];
            var bondLengthDataMol = mol.getBondLengthData();
            bondLengthData.cnt += bondLengthDataMol.cnt;
            bondLengthData.totalLength += bondLengthDataMol.totalLength;
        }
        var avgBondLength = 1/(bondLengthData.cnt == 0 ? 1 : bondLengthData.totalLength / bondLengthData.cnt);
	for (j = 0; j < mols.length; ++j) {
            mol = mols[j];
            mol.scale(avgBondLength);
        }
        
	for (j = 0; j < mols.length; ++j) {
            mol = mols[j];
            var bb = mol.getCoordBoundingBoxObj();
            if (!bb)
                continue;

            var fragmentType = (j < nReactants ? chem.Struct.FRAGMENT.REACTANT :
                    (j < nReactants + nProducts ? chem.Struct.FRAGMENT.PRODUCT :
                            chem.Struct.FRAGMENT.AGENT));
            if (fragmentType == chem.Struct.FRAGMENT.REACTANT) {
                bbReact.push(bb);
                molReact.push(mol);
            } else if (fragmentType == chem.Struct.FRAGMENT.AGENT) {
                bbAgent.push(bb);
                molAgent.push(mol);
            } else if (fragmentType == chem.Struct.FRAGMENT.PRODUCT) {
                bbProd.push(bb);
                molProd.push(mol);
            }

            mol.atoms.each(function(aid, atom){
                atom.rxnFragmentType = fragmentType;
            });
	}

    // reaction fragment layout
	var xorig = 0;
    var shiftMol = function(ret, mol, bb, xorig, over) {
        var d = new util.Vec2(xorig - bb.min.x, over ? 1 - bb.min.y : -(bb.min.y + bb.max.y) / 2);
        mol.atoms.each(function(aid, atom){
            atom.pp.add_(d);
        });
        mol.sgroups.each(function(id, item){
            if (item.pp)
                item.pp.add_(d);
        });
        bb.min.add_(d);
        bb.max.add_(d);
        mol.mergeInto(ret);
        return bb.max.x - bb.min.x;
    };

    for (j = 0; j < molReact.length; ++j) {
        xorig += shiftMol(ret, molReact[j], bbReact[j], xorig, false) + 2.0;
    }
    xorig += 2.0;
    for (j = 0; j < molAgent.length; ++j) {
        xorig += shiftMol(ret, molAgent[j], bbAgent[j], xorig, true) + 2.0;
    }
    xorig += 2.0;

    for (j = 0; j < molProd.length; ++j) {
        xorig += shiftMol(ret, molProd[j], bbProd[j], xorig, false) + 2.0;
    }

    var bb1, bb2, x, y, bbReactAll = null, bbProdAll = null;
	for (j = 0; j <	bbReact.length - 1; ++j) {
		bb1 = bbReact[j];
		bb2 = bbReact[j+1];

		x = (bb1.max.x + bb2.min.x) / 2;
		y = (bb1.max.y + bb1.min.y + bb2.max.y + bb2.min.y) / 4;

		ret.rxnPluses.add(new chem.Struct.RxnPlus({'pp':new util.Vec2(x, y)}));
	}
	for (j = 0; j <	bbReact.length; ++j) {
		if (j == 0) {
			bbReactAll = {};
			bbReactAll.max = new util.Vec2(bbReact[j].max);
			bbReactAll.min = new util.Vec2(bbReact[j].min);
		} else {
			bbReactAll.max = util.Vec2.max(bbReactAll.max, bbReact[j].max);
			bbReactAll.min = util.Vec2.min(bbReactAll.min, bbReact[j].min);
		}
	}
	for (j = 0; j <	bbProd.length - 1; ++j) {
		bb1 = bbProd[j];
		bb2 = bbProd[j+1];

		x = (bb1.max.x + bb2.min.x) / 2;
		y = (bb1.max.y + bb1.min.y + bb2.max.y + bb2.min.y) / 4;

		ret.rxnPluses.add(new chem.Struct.RxnPlus({'pp':new util.Vec2(x, y)}));
	}
	for (j = 0; j <	bbProd.length; ++j) {
		if (j == 0) {
			bbProdAll = {};
			bbProdAll.max = new util.Vec2(bbProd[j].max);
			bbProdAll.min = new util.Vec2(bbProd[j].min);
		} else {
			bbProdAll.max = util.Vec2.max(bbProdAll.max, bbProd[j].max);
			bbProdAll.min = util.Vec2.min(bbProdAll.min, bbProd[j].min);
		}
	}
	bb1 = bbReactAll;
	bb2 = bbProdAll;
	if (!bb1 && !bb2)
		throw new Error("reaction must contain at least one product or reactant");
	var v1 = bb1 ? new util.Vec2(bb1.max.x, (bb1.max.y + bb1.min.y) / 2) : null;
	var v2 = bb2 ? new util.Vec2(bb2.min.x, (bb2.max.y + bb2.min.y) / 2) : null;
	var defaultOffset = 3;
	if (!v1)
		v1 = new util.Vec2(v2.x - defaultOffset, v2.y);
	if (!v2)
		v2 = new util.Vec2(v1.x + defaultOffset, v1.y);
	var v = util.Vec2.lc2(v1, 0.5, v2, 0.5);

	ret.rxnArrows.add(new chem.Struct.RxnArrow({'pp':v}));
	ret.isReaction = true;
	return ret;
};

chem.Molfile.rgMerge = function (scaffold, rgroups) /* chem.Struct */
{
	var ret = new chem.Struct();

    scaffold.mergeInto(ret, null, null, false, true);
    for (var rgid in rgroups) {
        for (var j = 0; j < rgroups[rgid].length; ++j) {
            var ctab = rgroups[rgid][j];
            ctab.rgroups.set(rgid, new chem.Struct.RGroup());
            var frid = ctab.frags.add(new chem.Struct.Fragment());
            ctab.rgroups.get(rgid).frags.add(frid);
            ctab.atoms.each(function(aid, atom) {atom.fragment = frid;});
            ctab.mergeInto(ret);
        }
    }

	return ret;
};

chem.Molfile.parseRg2000 = function (/* string[] */ ctabLines) /* chem.Struct */
{
	var mf = chem.Molfile;
	ctabLines = ctabLines.slice(7);
    if (ctabLines[0].strip() != '$CTAB')
        throw new Error('RGFile format invalid');
    var i = 1;while (ctabLines[i][0] != '$') i++;
    if (ctabLines[i].strip() != '$END CTAB')
        throw new Error('RGFile format invalid');
    var coreLines = ctabLines.slice(1, i);
	ctabLines = ctabLines.slice(i+1);
    var fragmentLines = {};
    while (true) {
        if (ctabLines.length == 0)
            throw new Error('Unexpected end of file');
        var line = ctabLines[0].strip();
        if (line == '$END MOL') {
            ctabLines = ctabLines.slice(1);
            break;
        }
        if (line != '$RGP')
            throw new Error('RGFile format invalid');
        var rgid = ctabLines[1].strip() - 0;
        fragmentLines[rgid] = [];
        ctabLines = ctabLines.slice(2);
        while (true) {
            if (ctabLines.length == 0)
                throw new Error('Unexpected end of file');
            line = ctabLines[0].strip();
            if (line == '$END RGP') {
                ctabLines = ctabLines.slice(1);
                break;
            }
            if (line != '$CTAB')
                throw new Error('RGFile format invalid');
            i = 1;while (ctabLines[i][0] != '$') i++;
            if (ctabLines[i].strip() != '$END CTAB')
                throw new Error('RGFile format invalid');
            fragmentLines[rgid].push(ctabLines.slice(1, i));
            ctabLines = ctabLines.slice(i+1);
        }
    }

    var core = chem.Molfile.parseCTab(coreLines), frag = {};
    if (chem.Molfile.loadRGroupFragments) {
        for (var id in fragmentLines) {
            frag[id] = [];
            for (var j = 0; j < fragmentLines[id].length; ++j) {
                frag[id].push(chem.Molfile.parseCTab(fragmentLines[id][j]));
            }
        }
    }
	return mf.rgMerge(core, frag);
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.chem || !util.Vec2 || !util.Pool)
	throw new Error("Vec2, Pool should be defined first");
chem.SGroup = function (type)
{
	if (!type || !(type in chem.SGroup.TYPES))
		throw new Error("Invalid or unsupported s-group type");

	this.type = type;
	this.id = -1;
	chem.SGroup.equip(this, type);
	this.label = -1;
	this.bracketBox = null;
    this.bracketDir = new util.Vec2(1,0);
    this.areas = [];

	this.highlight = false;
	this.highlighting = null;
	this.selected = false;
	this.selectionPlate = null;

	this.atoms = [];
	this.patoms = [];
	this.bonds = [];
	this.xBonds = [];
	this.neiAtoms = [];
	this.pp = null;
	this.data = {
		'mul': 1, // multiplication count for MUL group
		'connectivity': 'ht', // head-to-head, head-to-tail or either-unknown
		'name' : '',
		'subscript' : '',

		// data s-group fields
		'attached' : false,
		'absolute' : true,
		'showUnits' : false,
		'nCharsToDisplay' : -1,
		'tagChar' : '',
		'daspPos' : 1,
		'fieldType' : 'F',
		'fieldName' : '',
		'units' : '',
		'query' : '',
		'queryOp' : ''
	}
};

// TODO: these methods should be overridden
//      and should only accept valid attributes for each S-group type.
//      The attributes should be accessed via these methods only and not directly through this.data.
// stub
chem.SGroup.prototype.getAttr = function (attr) {
    return this.data[attr];
};

// TODO: should be group-specific
chem.SGroup.prototype.getAttrs = function () {
    var attrs = {};
    for (var attr in this.data)
        attrs[attr] = this.data[attr];
    return attrs;
};

// stub
chem.SGroup.prototype.setAttr = function (attr, value) {
    var oldValue = this.data[attr];
    this.data[attr] = value;
    return oldValue;
};

// stub
chem.SGroup.prototype.checkAttr = function (attr, value) {
    return this.data[attr] == value;
};

chem.SGroup.equip = function (sgroup, type) {
	var impl = chem.SGroup.TYPES[type];
	for (var method in impl)
		sgroup[method] = impl[method];
};

chem.SGroup.numberArrayToString = function (numbers, map) {
	var str = util.stringPadded(numbers.length, 3);
	for (var i = 0; i < numbers.length; ++i) {
		str += ' ' + util.stringPadded(map[numbers[i]], 3);
	}
	return str;
};

chem.SGroup.addGroup = function (mol, sg, atomMap)
{
	// add the group to the molecule
	sg.id = mol.sgroups.add(sg);

	// apply type-specific post-processing
	sg.postLoad(mol, atomMap);

	// mark atoms in the group as belonging to it
	for (var s = 0; s < sg.atoms.length; ++s)
            if (mol.atoms.has(sg.atoms[s]))
                util.Set.add(mol.atoms.get(sg.atoms[s]).sgs, sg.id);

	return sg.id;
};

chem.SGroup.bracketsToMolfile = function (mol, sg, idstr) {
	var bb = chem.SGroup.getObjBBox(sg.atoms, mol);
	bb = bb.extend(new util.Vec2(0.4, 0.4));
        bb.p0 = bb.p0.yComplement();
        bb.p1 = bb.p1.yComplement();
	var coord = [
		[bb.p0.x, bb.p1.y, bb.p0.x, bb.p0.y],
		[bb.p1.x, bb.p0.y, bb.p1.x, bb.p1.y]
	];
	var lines = [];
	for (var j = 0; j < coord.length; ++j) {
		var line = 'M  SDI ' + idstr + util.paddedInt(4, 3);
		for (var i = 0; i < coord[j].length; ++i) {
			line += util.paddedFloat(coord[j][i], 10, 4);
		}
		lines.push(line);
	}

	return lines;
};

chem.SGroup.filterAtoms = function (atoms, map) {
	var newAtoms = [];
	for (var i = 0; i < atoms.length; ++i) {
		var aid = atoms[i];
		if (typeof(map[aid]) != "number") {
			newAtoms.push(aid);
		} else if (map[aid] >= 0) {
			newAtoms.push(map[aid]);
		} else {
			newAtoms.push(-1);
		}
	}
	return newAtoms;
};

chem.SGroup.removeNegative = function (atoms) {
	var newAtoms = [];
	for (var j = 0; j < atoms.length; ++j)
		if (atoms[j] >= 0)
			newAtoms.push(atoms[j]);
	return newAtoms;
};

chem.SGroup.filter = function (mol, sg, atomMap)
{
	sg.atoms = chem.SGroup.removeNegative(chem.SGroup.filterAtoms(sg.atoms, atomMap));
};

chem.SGroup.clone = function (sgroup, aidMap, bidMap)
{
	var cp = new chem.SGroup(sgroup.type);

	for (var field in sgroup.data) { // TODO: remove all non-primitive properties from 'data'
		cp.data[field] = sgroup.data[field];
	}
	cp.atoms = util.mapArray(sgroup.atoms, aidMap);
	cp.pp = sgroup.pp;
	cp.bracketBox = sgroup.bracketBox;
	cp.patoms = null;
	cp.bonds = null;
	cp.allAtoms = sgroup.allAtoms;
	return cp;
};

chem.SGroup.addAtom = function (sgroup, aid)
{
	sgroup.atoms.push(aid);
};

chem.SGroup.removeAtom = function (sgroup, aid)
{
	for (var i = 0; i < sgroup.atoms.length; ++i) {
		if (sgroup.atoms[i] == aid) {
			sgroup.atoms.splice(i, 1);
			return;
		}
	}
	throw new Error("The atom is not found in the given s-group");
};

chem.SGroup.getCrossBonds = function (inBonds, xBonds, mol, parentAtomSet) {
    mol.bonds.each(function(bid, bond){
        if (util.Set.contains(parentAtomSet, bond.begin) && util.Set.contains(parentAtomSet, bond.end)) {
            if (inBonds != null)
                inBonds.push(bid);
        } else if (util.Set.contains(parentAtomSet, bond.begin) || util.Set.contains(parentAtomSet, bond.end)) {
            if (xBonds != null)
                xBonds.push(bid);
        }
    }, this);
}

chem.SGroup.bracketPos = function (sg, remol, xbonds) {
    var atoms = sg.atoms;
    if (xbonds.length != 2) {
        sg.bracketDir = new util.Vec2(1, 0);
    } else {
        var b1 = remol.bonds.get(xbonds[0]), b2 = remol.bonds.get(xbonds[1]);
        var p1 = b1.b.center, p2 = b2.b.center;
        sg.bracketDir = util.Vec2.diff(p2, p1).normalized();
    }
    var d = sg.bracketDir;
    var n = d.rotateSC(1, 0);

    var bb = null;
    var render = remol.render;
    var settings = render.settings;
    for (var i = 0; i < atoms.length; ++i) {
        var aid = atoms[i];
        var atom = remol.atoms.get(aid);
        var bba = atom.visel.boundingBox;
        if (bba == null) {
            var p = new util.Vec2(util.Vec2.dot(atom.a.pp, d), util.Vec2.dot(atom.a.pp, n));
            bba = new util.Box2Abs(p,p);
            var ext = new util.Vec2(0.05 * 3, 0.05 * 3);
            bba = bba.extend(ext, ext);
        } else {
            bba = bba.transform(render.scaled2obj, render);
        }
        bb = (bb == null) ? bba : util.Box2Abs.union(bb, bba);
    }
    var vext = new util.Vec2(0.05 * 2, 0.05 * 4);
    if (bb != null)
        bb = bb.extend(vext, vext);

    sg.bracketBox = bb;
};

chem.SGroup.drawBrackets = function (set, render, paper, settings, styles, bb, d, n) {
    d = d || new util.Vec2(1, 0);
    n = n || new util.Vec2(0, 1);
    var bracketWidth = Math.min(0.25, bb.sz().x * 0.3);
    var a0 = util.Vec2.lc2(d, bb.p0.x, n, bb.p0.y);
    var a1 = util.Vec2.lc2(d, bb.p0.x, n, bb.p1.y);
    var b0 = util.Vec2.lc2(d, bb.p1.x, n, bb.p0.y);
    var b1 = util.Vec2.lc2(d, bb.p1.x, n, bb.p1.y);
    var a02 = a0.addScaled(d, bracketWidth);
    var a12 = a1.addScaled(d, bracketWidth);
    var b02 = b0.addScaled(d, -bracketWidth);
    var b12 = b1.addScaled(d, -bracketWidth);

    a0 = render.obj2scaled(a0);
    a1 = render.obj2scaled(a1);
    b0 = render.obj2scaled(b0);
    b1 = render.obj2scaled(b1);
    a02 = render.obj2scaled(a02);
    a12 = render.obj2scaled(a12);
    b02 = render.obj2scaled(b02);
    b12 = render.obj2scaled(b12);

    var leftBracket = paper.path("M{0},{1}L{2},{3}L{4},{5}L{6},{7}",
        a02.x, a02.y, a0.x, a0.y, a1.x, a1.y, a12.x, a12.y)
        .attr(styles.sgroupBracketStyle);

    var rightBracket = paper.path("M{0},{1}L{2},{3}L{4},{5}L{6},{7}",
        b02.x, b02.y, b0.x, b0.y, b1.x, b1.y, b12.x, b12.y)
        .attr(styles.sgroupBracketStyle);
    set.push(leftBracket, rightBracket);
};

chem.SGroup.getObjBBox = function (atoms, mol)
{
	if (atoms.length == 0)
		throw new Error("Atom list is empty");

	var a0 = mol.atoms.get(atoms[0]).pp;
	var bb = new util.Box2Abs(a0, a0);
	for (var i = 1; i < atoms.length; ++i) {
		var aid = atoms[i];
		var atom = mol.atoms.get(aid);
		var p = atom.pp;
		bb = bb.include(p);
	}
	return bb;
};

chem.SGroup.getBBox = function (atoms, remol) {
	var bb = null;
	var render = remol.render;
	var settings = render.settings;
	for (var i = 0; i < atoms.length; ++i) {
		var aid = atoms[i];
		var atom = remol.atoms.get(aid);
		var bba = atom.visel.boundingBox;
		if (bba == null) {
			var p = atom.a.pp;
			bba = new util.Box2Abs(p,p);
			var ext = new util.Vec2(0.15, 0.15);
			bba = bba.extend(ext, ext);
		}
		bb = (bb == null) ? bba : util.Box2Abs.union(bb, bba);
	}
	return bb;
};

chem.SGroup.makeAtomBondLines = function (prefix, idstr, ids, map) {
	if (!ids)
		return [];
	var lines = [];
	for (var i = 0; i < Math.floor((ids.length + 14) / 15); ++i) {
		var rem = Math.min(ids.length - 15 * i, 15);
		var salLine = 'M  ' + prefix + ' ' + idstr + ' ' + util.paddedInt(rem, 2);
		for (var j = 0; j < rem; ++j) {
			salLine += ' ' + util.paddedInt(map[ids[i * 15 + j]], 3);
		}
		lines.push(salLine);
	}
	return lines;
};

chem.SGroup.getAtoms = function (mol, sg) {
	if (!sg.allAtoms)
		return sg.atoms;
	var atoms = [];
	mol.atoms.each(function(aid){
		atoms.push(aid);
	});
	return atoms;
};

chem.SGroup.GroupMul = {
	draw: function (remol) {
		var render = remol.render;
		var settings = render.settings;
		var styles = render.styles;
		var paper = render.paper;
		var set = paper.set();
        var inBonds = [], xBonds = [];
        chem.SGroup.getCrossBonds(inBonds, xBonds, remol.molecule, util.Set.fromList(this.atoms));
        chem.SGroup.bracketPos(this, remol, xBonds);
        var bb = this.bracketBox;
        var d = this.bracketDir, n = d.rotateSC(1, 0);
        this.areas = [bb];
	chem.SGroup.drawBrackets(set, render, paper, settings, styles, bb, d, n);
        var idxOffset = 0.25;
        var idxPos = util.Vec2.lc(d, d.x < 0 ? bb.p0.x - idxOffset : bb.p1.x + idxOffset,
            n, d.x < 0 ? bb.p0.y : bb.p1.y);
        idxPos = render.obj2scaled(idxPos);
        var multIndex = paper.text(idxPos.x, idxPos.y, this.data.mul)
		.attr({
			'font' : settings.font,
			'font-size' : settings.fontszsub
		});
		var multIndexBox = rnd.relBox(multIndex.getBBox());
		multIndex.translate(0.5 * multIndexBox.width, -0.3 * multIndexBox.height);
		set.push(multIndex);
		return set;
	},

	saveToMolfile: function (mol, sgMap, atomMap, bondMap) {
		var idstr = util.stringPadded(sgMap[this.id], 3);

		var lines = [];
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SAL', idstr, util.idList(this.atomSet), atomMap)); // TODO: check atomSet
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SPA', idstr, util.idList(this.parentAtomSet), atomMap));
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SBL', idstr, this.bonds, bondMap));
		var smtLine = 'M  SMT ' + idstr + ' ' + this.data.mul;
		lines.push(smtLine);
		lines = lines.concat(chem.SGroup.bracketsToMolfile(mol, this, idstr));
		return lines.join('\n');
	},

	prepareForSaving: function (mol) {
		var i,j;
		this.atomSet = util.Set.fromList(this.atoms);
        this.parentAtomSet = util.Set.clone(this.atomSet);
		var inBonds = [];
		var xBonds = [];

		mol.bonds.each(function(bid, bond){
			if (util.Set.contains(this.parentAtomSet, bond.begin) && util.Set.contains(this.parentAtomSet, bond.end))
				inBonds.push(bid);
			else if (util.Set.contains(this.parentAtomSet, bond.begin) || util.Set.contains(this.parentAtomSet,bond.end))
				xBonds.push(bid);
		}, this);
		if (xBonds.length != 0 && xBonds.length != 2)
			throw {'id':this.id, 'error-type':'cross-bond-number', 'message':"Unsupported cross-bonds number"};

		var xAtom1 = -1,
		xAtom2 = -1;
		var crossBond = null;
		if (xBonds.length == 2) {
			var bond1 = mol.bonds.get(xBonds[0]);
			if (util.Set.contains(this.parentAtomSet, bond1.begin)) {
				xAtom1 = bond1.begin;
			} else {
				xAtom1 = bond1.end;
			}
			var bond2 = mol.bonds.get(xBonds[1]);
			if (util.Set.contains(this.parentAtomSet, bond2.begin)) {
				xAtom2 = bond2.begin;
			} else {
				xAtom2 = bond2.end;
			}
			crossBond = bond2;
		}

		var amap = null;
		var tailAtom = xAtom1;
		for (j = 0; j < this.data.mul - 1; ++j) {
			amap = {};
			for (i = 0; i < this.atoms.length; ++i) {
				var aid = this.atoms[i];
				var atom = mol.atoms.get(aid);
				var aid2 = mol.atoms.add(new chem.Struct.Atom(atom));
				this.atomSet[aid2] = 1;
				amap[aid] = aid2;
			}
			for (i = 0; i < inBonds.length; ++i) {
				var bond = mol.bonds.get(inBonds[i]);
				var newBond = new chem.Struct.Bond(bond);
				newBond.begin = amap[newBond.begin];
				newBond.end = amap[newBond.end];
				mol.bonds.add(newBond);
			}
			if (crossBond != null) {
				var newCrossBond = new chem.Struct.Bond(crossBond);
				newCrossBond.begin = tailAtom;
				newCrossBond.end = amap[xAtom2];
				mol.bonds.add(newCrossBond);
				tailAtom = amap[xAtom1];
			}
		}
		if (tailAtom >= 0) {
			var xBond2 = mol.bonds.get(xBonds[0]);
			if (xBond2.begin == xAtom1)
				xBond2.begin = tailAtom;
			else
				xBond2.end = tailAtom;
		}
		this.bonds = xBonds;
	},

	postLoad: function (mol, atomMap)
	{
		this.data.mul = this.data.subscript - 0;
		var atomReductionMap = {};

		this.atoms = chem.SGroup.filterAtoms(this.atoms, atomMap);
		this.patoms = chem.SGroup.filterAtoms(this.patoms, atomMap);

		// mark repetitions for removal
		for (var k = 1; k < this.data.mul; ++k) {
			for (var m = 0; m < this.patoms.length; ++m) {
				var raid = this.atoms[k * this.patoms.length + m];
				if (raid < 0)
					continue;
				if (this.patoms[m] < 0) {
					throw new Error("parent atom missing");
				}
//				mol.atoms.get(raid).pp.y -= 3*k; // for debugging purposes
				atomReductionMap[raid] = this.patoms[m]; // "merge" atom in parent
			}
		}
		this.patoms = chem.SGroup.removeNegative(this.patoms);

		var patomsMap = util.identityMap(this.patoms);

		var bondsToRemove = [];
		mol.bonds.each(function(bid, bond){
			var beginIn = bond.begin in atomReductionMap;
			var endIn = bond.end in atomReductionMap;
			// if both adjacent atoms of a bond are to be merged, remove it
			if (beginIn && endIn
				|| beginIn && bond.end in patomsMap
				|| endIn && bond.begin in patomsMap) {
				bondsToRemove.push(bid);
			// if just one atom is merged, modify the bond accordingly
			} else if (beginIn) {
				bond.begin = atomReductionMap[bond.begin];
			} else if (endIn) {
				bond.end = atomReductionMap[bond.end];
			}
		}, this);

		// apply removal lists
		for (var b = 0; b < bondsToRemove.length; ++b) {
			mol.bonds.remove(bondsToRemove[b]);
		}
		for (var a in atomReductionMap) {
			mol.atoms.remove(a);
			atomMap[a] = -1;
		}
		this.atoms = this.patoms;
		this.patoms = null;
	}
};

chem.SGroup.GroupSru = {
	draw: function (remol) {
		var render = remol.render;
		var settings = render.settings;
		var styles = render.styles;
		var paper = render.paper;
		var set = paper.set();
        var inBonds = [], xBonds = [];
        chem.SGroup.getCrossBonds(inBonds, xBonds, remol.molecule, util.Set.fromList(this.atoms));
        chem.SGroup.bracketPos(this, remol, xBonds);
        var bb = this.bracketBox;
        var d = this.bracketDir, n = d.rotateSC(1, 0);
        this.areas = [bb];
        chem.SGroup.drawBrackets(set, render, paper, settings, styles, bb, d, n);
		var connectivity = this.data.connectivity || 'eu';
        var idxOffset = 0.25;
		if (connectivity != 'ht') {
            var idxPos = util.Vec2.lc(d, d.x < 0 ? bb.p0.x - idxOffset : bb.p1.x + idxOffset,
                n, d.x >= 0 ? bb.p0.y : bb.p1.y);
            idxPos = render.obj2scaled(idxPos);
			var connectivityIndex = paper.text(idxPos.x, idxPos.y, connectivity)
			.attr({
				'font' : settings.font,
				'font-size' : settings.fontszsub
			});
			set.push(connectivityIndex);
		}
		this.data.subscript = this.data.subscript || 'n';
        idxPos = util.Vec2.lc(d, d.x < 0 ? bb.p0.x - idxOffset : bb.p1.x + idxOffset,
            n, d.x < 0 ? bb.p0.y : bb.p1.y);
        idxPos = render.obj2scaled(idxPos);
		var subscript = paper.text(idxPos.x, idxPos.y, this.data.subscript)
		.attr({
			'font' : settings.font,
			'font-size' : settings.fontszsub
		});
		set.push(subscript);
		return set;
	},

	saveToMolfile: function (mol, sgMap, atomMap, bondMap) {
		var idstr = util.stringPadded(sgMap[this.id], 3);

		var lines = [];
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SAL', idstr, this.atoms, atomMap));
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SBL', idstr, this.bonds, bondMap));
		lines = lines.concat(chem.SGroup.bracketsToMolfile(mol, this, idstr));
		return lines.join('\n');
	},

	prepareForSaving: function (mol) {
		var xBonds = [];
		mol.bonds.each(function(bid, bond){
			var a1 = mol.atoms.get(bond.begin);
			var a2 = mol.atoms.get(bond.end);
			if (util.Set.contains(a1.sgs, this.id) && !util.Set.contains(a2.sgs, this.id) ||
				util.Set.contains(a2.sgs, this.id) && !util.Set.contains(a1.sgs, this.id))
				xBonds.push(bid);
		},this);
		this.bonds = xBonds;
	},

	postLoad: function (mol, atomMap) {
		this.data.connectivity = (this.data.connectivity || 'EU').strip().toLowerCase();
	}
};

chem.SGroup.GroupSup = {
	draw: function (remol) {
		var render = remol.render;
		var settings = render.settings;
		var styles = render.styles;
		var paper = render.paper;
		var set = paper.set();
        var inBonds = [], xBonds = [];
        chem.SGroup.getCrossBonds(inBonds, xBonds, remol.molecule, util.Set.fromList(this.atoms));
        chem.SGroup.bracketPos(this, remol, xBonds);
        var bb = this.bracketBox;
        var d = this.bracketDir, n = d.rotateSC(1, 0);
        this.areas = [bb];
        chem.SGroup.drawBrackets(set, render, paper, settings, styles, bb, d, n);
		if (this.data.name) {
            var idxOffset = 0.25;
            var idxPos = util.Vec2.lc(d, d.x < 0 ? bb.p0.x - idxOffset : bb.p1.x + idxOffset,
                n, d.x < 0 ? bb.p0.y : bb.p1.y);
            idxPos = render.obj2scaled(idxPos);
			var name = paper.text(idxPos.x, idxPos.y, this.data.name)
			.attr({
				'font' : settings.font,
				'font-size' : settings.fontszsub,
				'font-style' : 'italic'
			});
			var nameBox = rnd.relBox(name.getBBox());
			name.translate(0.5 * nameBox.width * d.x, 0);
			set.push(name);
		}
		return set;
	},

	saveToMolfile: function (mol, sgMap, atomMap, bondMap) {
		var idstr = util.stringPadded(sgMap[this.id], 3);

		var lines = [];
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SAL', idstr, this.atoms, atomMap));
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SBL', idstr, this.bonds, bondMap));
		if (this.data.name && this.data.name != '')
			lines.push('M  SMT ' + idstr + ' ' + this.data.name);
		return lines.join('\n');
	},

	prepareForSaving: function (mol) {
		// This code is also used for GroupSru and should be moved into a separate common method
		// It seems that such code should be used for any sgroup by this this should be checked
		var xBonds = [];
		mol.bonds.each(function(bid, bond){
			var a1 = mol.atoms.get(bond.begin);
			var a2 = mol.atoms.get(bond.end);
			if (util.Set.contains(a1.sgs, this.id) && !util.Set.contains(a2.sgs, this.id) ||
				util.Set.contains(a2.sgs, this.id) && !util.Set.contains(a1.sgs, this.id))
				xBonds.push(bid);
		},this);
		this.bonds = xBonds;
	},

	postLoad: function (mol, atomMap) {
		this.data.name = (this.data.subscript || '').strip();
	}
};

chem.SGroup.GroupGen = {
	draw: function (remol) {
		var render = remol.render;
		var settings = render.settings;
		var styles = render.styles;
		var paper = render.paper;
		var set = paper.set();
        var inBonds = [], xBonds = [];
        chem.SGroup.getCrossBonds(inBonds, xBonds, remol.molecule, util.Set.fromList(this.atoms));
        chem.SGroup.bracketPos(this, remol, xBonds);
        var bb = this.bracketBox;
        var d = this.bracketDir, n = d.rotateSC(1, 0);
        this.areas = [bb];
        chem.SGroup.drawBrackets(set, render, paper, settings, styles, bb, d, n);
		return set;
	},

	saveToMolfile: function (mol, sgMap, atomMap, bondMap) {
		var idstr = util.stringPadded(sgMap[this.id], 3);

		var lines = [];
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SAL', idstr, this.atoms, atomMap));
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SBL', idstr, this.bonds, bondMap));
		lines = lines.concat(chem.SGroup.bracketsToMolfile(mol, this, idstr));
		return lines.join('\n');
	},

	prepareForSaving: function (mol) {
	},

	postLoad: function (mol, atomMap) {
	}
};

chem.SGroup.getMassCentre = function (mol, atoms) {
	var c = new util.Vec2(); // mass centre
	for (var i = 0; i < atoms.length; ++i) {
		c = c.addScaled(mol.atoms.get(atoms[i]).pp, 1.0 / atoms.length);
	}
	return c;
};

chem.SGroup.setPos = function (remol, sg, pos) {
	sg.pp = pos;
};

chem.SGroup.GroupDat = {
	showValue: function (paper, pos, sg, settings) {
		var name = paper.text(pos.x, pos.y, sg.data.fieldValue)
		.attr({
			'font' : settings.font,
			'font-size' : settings.fontsz
		});
		return name;
	},

	draw: function (remol) {
		var render = remol.render;
		var settings = render.settings;
		var paper = render.paper;
		var set = paper.set();
		var absolute = this.data.absolute || this.allAtoms;
		var atoms = chem.SGroup.getAtoms(remol, this);
		var i;
        var inBonds = [], xBonds = [];
        chem.SGroup.getCrossBonds(inBonds, xBonds, remol.molecule, util.Set.fromList(this.atoms));
        chem.SGroup.bracketPos(this, remol, xBonds);
        this.areas = [this.bracketBox];
		if (this.pp == null) {
			chem.SGroup.setPos(remol, this, this.bracketBox.p1.add(new util.Vec2(0.5, 0.5)));
		}
        var ps = this.pp.scaled(settings.scaleFactor);

        if (this.data.attached) {
                for (i = 0; i < atoms.length; ++i) {
                var atom = remol.atoms.get(atoms[i]);
                var p = render.ps(atom.a.pp);
                var bb = atom.visel.boundingBox;
                if (bb != null) {
                    p.x = Math.max(p.x, bb.p1.x);
                }
                p.x += settings.lineWidth; // shift a bit to the right
                var name_i = this.showValue(paper, p, this, settings);
                var box_i = rnd.relBox(name_i.getBBox());
                name_i.translate(0.5 * box_i.width, -0.3 * box_i.height);
                set.push(name_i);
                var sbox_i = util.Box2Abs.fromRelBox(rnd.relBox(name_i.getBBox()));
                this.areas.push(sbox_i.transform(render.scaled2obj, render));
            }
        } else {
            var name = this.showValue(paper, ps, this, settings);
            var box = rnd.relBox(name.getBBox());
            name.translate(0.5 * box.width, -0.5 * box.height);
            set.push(name);
            var sbox = util.Box2Abs.fromRelBox(rnd.relBox(name.getBBox()));
            this.dataArea = sbox.transform(render.scaled2obj, render);
            if (!remol.sgroupData.has(this.id))
                remol.sgroupData.set(this.id, new rnd.ReDataSGroupData(this));
        }
        return set;
	},

	saveToMolfile: function (mol, sgMap, atomMap, bondMap) {
		var idstr = util.stringPadded(sgMap[this.id], 3);

		var data = this.data;
		var pp = this.pp;
        if (!data.absolute)
            pp = pp.sub(chem.SGroup.getMassCentre(mol, this.atoms));
		var lines = [];
		lines = lines.concat(chem.SGroup.makeAtomBondLines('SAL', idstr, this.atoms, atomMap));
		var sdtLine = 'M  SDT ' + idstr +
		' ' + util.stringPadded(data.fieldName, 30, true) +
		util.stringPadded(data.fieldType, 2) +
		util.stringPadded(data.units, 20, true) +
		util.stringPadded(data.query, 2) +
		util.stringPadded(data.queryOp, 3);
		lines.push(sdtLine);
		var sddLine = 'M  SDD ' + idstr +
		' ' + util.paddedFloat(pp.x, 10, 4) + util.paddedFloat(-pp.y, 10, 4) +
		'    ' + // ' eee'
		(data.attached ? 'A' : 'D') + // f
		(data.absolute ? 'A' : 'R') + // g
		(data.showUnits ? 'U' : ' ') + // h
		'   ' + //  i
		(data.nCharnCharsToDisplay >= 0 ? util.paddedInt(data.nCharnCharsToDisplay, 3) : 'ALL') + // jjj
		'  1   ' + // 'kkk ll '
		util.stringPadded(data.tagChar, 1) + // m
		'  ' + util.paddedInt(data.daspPos, 1) + // n
		'  '; // oo
		lines.push(sddLine);
		var str = data.fieldValue;
		var charsPerLine = 69;
		while (str.length > charsPerLine) {
			lines.push('M  SCD ' + idstr + ' ' + str.slice(0, charsPerLine));
			str = str.slice(69);
		}
		lines.push('M  SED ' + idstr + ' ' + util.stringPadded(str, charsPerLine, true));
		return lines.join('\n');
	},

	prepareForSaving: function (mol) {
		this.atoms = chem.SGroup.getAtoms(mol, this);
	},

	postLoad: function (mol, atomMap) {
		var allAtomsInGroup = this.atoms.length == mol.atoms.count();
        if (!this.data.absolute)
            this.pp = this.pp.add(chem.SGroup.getMassCentre(mol, this.atoms));
		if (allAtomsInGroup &&
			(	this.data.fieldName == 'MDLBG_FRAGMENT_STEREO' ||
				this.data.fieldName == 'MDLBG_FRAGMENT_COEFFICIENT' ||
				this.data.fieldName == 'MDLBG_FRAGMENT_CHARGE')) {
			this.atoms = [];
			this.allAtoms = true;
		}
	}
};

chem.SGroup.TYPES = {
	'MUL': chem.SGroup.GroupMul,
	'SRU': chem.SGroup.GroupSru,
	'SUP': chem.SGroup.GroupSup,
	'DAT': chem.SGroup.GroupDat,
	'GEN': chem.SGroup.GroupGen
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.chem || !chem.Struct)
	throw new Error("Include MolData.js first");

chem.Struct.prototype.calcConn = function (aid)
{
	var conn = 0;
	var atom = this.atoms.get(aid);
	var hasAromatic = false;
	for (var i = 0; i < atom.neighbors.length; ++i) {
		var hb = this.halfBonds.get(atom.neighbors[i]);
		var bond = this.bonds.get(hb.bid);
		switch (bond.type) {
			case chem.Struct.BOND.TYPE.SINGLE:
				conn += 1;
				break;
			case chem.Struct.BOND.TYPE.DOUBLE:
				conn += 2;
				break;
			case chem.Struct.BOND.TYPE.TRIPLE:
				conn += 3;
				break;
			case chem.Struct.BOND.TYPE.AROMATIC:
				conn += 1;
				hasAromatic = true;
				break;
			default:
				return -1;
		}
	}
	if (hasAromatic)
		conn += 1;
	return conn;
};

chem.Struct.Atom.prototype.calcValence = function (conn)
{
	var atom = this;
	var charge = atom.charge;
	var label = atom.label;
	if (atom.isQuery()) {
		this.valence = -1;
		this.implicitH = -1;
		return true;
	}
	var elem = chem.Element.getElementByLabel(label);
	if (elem == null) {
		this.valence = -1;
		this.implicitH = 0;
		return true;
	}

	var groupno = chem.Element.elements.get(elem).group;
	var rad = chem.Struct.radicalElectrons(atom.radical);

	var valence = conn;
	var hyd = 0;
	var absCharge = Math.abs(charge);

	if (groupno == 1)
	{
		if (label == 'H' ||
			label == 'Li' || label == 'Na' || label == 'K' ||
			label == 'Rb' || label == 'Cs' || label == 'Fr')
			{
			valence = 1;
			hyd = 1 - rad - conn - absCharge;
		}
	}
	else if (groupno == 3)
	{
		if (label == 'B' || label == 'Al' || label == 'Ga' || label == 'In')
		{
			if (charge == -1)
			{
				valence = 4;
				hyd = 4 - rad - conn;
			}
			else
			{
				valence = 3;
				hyd = 3 - rad - conn - absCharge;
			}
		}
		else if (label == 'Tl')
		{
			if (charge == -1)
			{
				if (rad + conn <= 2)
				{
					valence = 2;
					hyd = 2 - rad - conn;
				}
				else
				{
					valence = 4;
					hyd = 4 - rad - conn;
				}
			}
			else if (charge == -2)
			{
				if (rad + conn <= 3)
				{
					valence = 3;
					hyd = 3 - rad - conn;
				}
				else
				{
					valence = 5;
					hyd = 5 - rad - conn;
				}
			}
			else
			{
				if (rad + conn + absCharge <= 1)
				{
					valence = 1;
					hyd = 1 - rad - conn - absCharge;
				}
				else
				{
					valence = 3;
					hyd = 3 - rad - conn - absCharge;
				}
			}
		}
	}
	else if (groupno == 4)
	{
		if (label == 'C' || label == 'Si' || label == 'Ge')
		{
			valence = 4;
			hyd = 4 - rad - conn - absCharge;
		}
		else if (label == 'Sn' || label == 'Pb')
		{
			if (conn + rad + absCharge <= 2)
			{
				valence = 2;
				hyd = 2 - rad - conn - absCharge;
			}
			else
			{
				valence = 4;
				hyd = 4 - rad - conn - absCharge;
			}
		}
	}
	else if (groupno == 5)
	{
		if (label == 'N' || label == 'P')
		{
			if (charge == 1)
			{
				valence = 4;
				hyd = 4 - rad - conn;
			}
			else if (charge == 2)
			{
				valence = 3;
				hyd = 3 - rad - conn;
			}
			else
			{
				if (label == 'N' || rad + conn + absCharge <= 3)
				{
					valence = 3;
					hyd = 3 - rad - conn - absCharge;
				}
				else // ELEM_P && rad + conn + absCharge > 3
				{
					valence = 5;
					hyd = 5 - rad - conn - absCharge;
				}
			}
		}
		else if (label == 'Bi' || label == 'Sb' || label == 'As')
		{
			if (charge == 1)
			{
				if (rad + conn <= 2 && label != 'As')
				{
					valence = 2;
					hyd = 2 - rad - conn;
				}
				else
				{
					valence = 4;
					hyd = 4 - rad - conn;
				}
			}
			else if (charge == 2)
			{
				valence = 3;
				hyd = 3 - rad - conn;
			}
			else
			{
				if (rad + conn <= 3)
				{
					valence = 3;
					hyd = 3 - rad - conn - absCharge;
				}
				else
				{
					valence = 5;
					hyd = 5 - rad - conn - absCharge;
				}
			}
		}
	}
	else if (groupno == 6)
	{
		if (label == 'O')
		{
			if (charge >= 1)
			{
				valence = 3;
				hyd = 3 - rad - conn;
			}
			else
			{
				valence = 2;
				hyd = 2 - rad - conn - absCharge;
			}
		}
		else if (label == 'S' || label == 'Se' || label == 'Po')
		{
			if (charge == 1)
			{
				if (conn <= 3)
				{
					valence = 3;
					hyd = 3 - rad - conn;
				}
				else
				{
					valence = 5;
					hyd = 5 - rad - conn;
				}
			}
			else
			{
				if (conn + rad + absCharge <= 2)
				{
					valence = 2;
					hyd = 2 - rad - conn - absCharge;
				}
				else if (conn + rad + absCharge <= 4)
				// See examples in PubChem
				// [S] : CID 16684216
				// [Se]: CID 5242252
				// [Po]: no example, just following ISIS/Draw logic here
				{
					valence = 4;
					hyd = 4 - rad - conn - absCharge;
				}
				else
				// See examples in PubChem
				// [S] : CID 46937044
				// [Se]: CID 59786
				// [Po]: no example, just following ISIS/Draw logic here
				{
					valence = 6;
					hyd = 6 - rad - conn - absCharge;
				}
			}
		}
		else if (label == 'Te')
		{
			if (charge == -1)
			{
				if (conn <= 2)
				{
					valence = 2;
					hyd = 2 - rad - conn - absCharge;
				}
			}
			else if (charge == 0 || charge == 2)
			{
				if (conn <= 2)
				{
					valence = 2;
					hyd = 2 - rad - conn - absCharge;
				}
				else if (conn <= 4)
				{
					valence = 4;
					hyd = 4 - rad - conn - absCharge;
				}
				else if (charge == 0 && conn <= 6)
				{
					valence = 6;
					hyd = 6 - rad - conn - absCharge;
				}
				else
					hyd = -1;
			}
		}
	}
	else if (groupno == 7)
	{
		if (label == 'F')
		{
			valence = 1;
			hyd = 1 - rad - conn - absCharge;
		}
		else if (label == 'Cl' || label == 'Br' ||
			label == 'I'  || label == 'At')
			{
			if (charge == 1)
			{
				if (conn <= 2)
				{
					valence = 2;
					hyd = 2 - rad - conn;
				}
				else if (conn == 3 || conn == 5 || conn >= 7)
					hyd = -1;
			}
			else if (charge == 0)
			{
				if (conn <= 1)
				{
					valence = 1;
					hyd = 1 - rad - conn;
				}
				// While the halogens can have valence 3, they can not have
				// hydrogens in that case.
				else if (conn == 2 || conn == 4 || conn == 6)
				{
					if (rad == 1)
					{
						valence = conn;
						hyd = 0;
					}
					else
						hyd = -1; // will throw an error in the end
				}
				else if (conn > 7)
					hyd = -1; // will throw an error in the end
			}
		}
	}

	this.valence = valence;
	this.implicitH = hyd;
	if (this.implicitH < 0)
	{
		this.valence = conn;
		this.implicitH = 0;
		this.badConn = true;
		return false;
	}
	return true;
};

chem.Struct.Atom.prototype.calcValenceMinusHyd = function (conn)
{
	var atom = this;
	var charge = atom.charge;
	var label = atom.label;
	var elem = chem.Element.getElementByLabel(label);
	if (elem == null)
		throw new Error("Element " + label + " unknown");
	if (elem < 0) { // query atom, skip
		this.valence = -1;
		this.implicitH = -1;
		return null;
	}

	var groupno = chem.Element.elements.get(elem).group;
	var rad = chem.Struct.radicalElectrons(atom.radical);

	if (groupno == 3)
	{
		if (label == 'B' || label == 'Al' || label == 'Ga' || label == 'In')
		{
			if (charge == -1)
				if (rad + conn <= 4)
					return rad + conn;
		}
	}
	else if (groupno == 5)
	{
		if (label == 'N' || label == 'P')
		{
			if (charge == 1)
				return rad + conn;
			if (charge == 2)
				return rad + conn;
		}
		else if (label == 'Sb' || label == 'Bi' || label == 'As')
		{
			if (charge == 1)
				return rad + conn;
			else if (charge == 2)
				return rad + conn;
		}
	}
	else if (groupno == 6)
	{
		if (label == 'O')
		{
			if (charge >= 1)
				return rad + conn;
		}
		else if (label == 'S'  || label == 'Se' || label == 'Po')
		{
			if (charge == 1)
				return rad + conn;
		}
	}
	else if (groupno == 7)
	{
		if (label == 'Cl' || label == 'Br' ||
			label == 'I'  || label == 'At')
			{
			if (charge == 1)
				return rad + conn;
		}
	}

	return rad + conn + Math.abs(charge);
};

chem.Struct.prototype.calcImplicitHydrogen = function (aid)
{
	var conn = this.calcConn(aid);
	var atom = this.atoms.get(aid);
	if (conn < 0) {
		atom.implicitH = -1;
		return;
	}
	atom.badConn = false;
	if (atom.explicitValence) {
		var elem = chem.Element.getElementByLabel(atom.label);
		atom.implicitH = 0;
		if (elem != null) {
			atom.implicitH = atom.valence - atom.calcValenceMinusHyd(conn);
			if (atom.implicitH < 0) {
				atom.implicitH = -1;
				atom.badConn = true;
			}
		}
	} else {
		atom.calcValence(conn);
	}
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.chem || !chem.Struct)
    throw new Error("Molecule should be defined first");

chem.Dfs = function (mol, atom_data, components, nReactants)
{
    this.molecule = mol;
    this.atom_data = atom_data;
    this.components = components;
    this.nComponentsInReactants = -1;
    this.nReactants = nReactants;

    this.vertices = new Array(this.molecule.atoms.count()); // Minimum size
    this.molecule.atoms.each(function (aid)
    {
        this.vertices[aid] = new chem.Dfs.VertexDesc();
    }, this);

    this.edges = new Array(this.molecule.bonds.count()); // Minimum size
    this.molecule.bonds.each(function (bid)
    {
        this.edges[bid] = new chem.Dfs.EdgeDesc();
    }, this);

    this.v_seq = new Array();
};

chem.Dfs.VertexDesc = function ()
{
    this.dfs_state = 0;       // 0 -- not on stack
                              // 1 -- on stack
                              // 2 -- removed from stack
    this.parent_vertex = 0;   // parent vertex in DFS tree
    this.parent_edge = 0;     // edge to parent vertex
    this.branches = 0;    // how many DFS branches go out from this vertex}
};

chem.Dfs.EdgeDesc = function ()
{
    this.opening_cycles = 0; // how many cycles are
                             // (i) starting with this edge
                             // and (ii) ending in this edge's first vertex
    this.closing_cycle = 0;  // 1 if this edge closes a cycle
};

chem.Dfs.SeqElem = function (v_idx, par_vertex, par_edge)
{
    this.idx = v_idx;                // index of vertex in _graph
    this.parent_vertex = par_vertex; // parent vertex in DFS tree
    this.parent_edge = par_edge;     // edge to parent vertex
};

chem.Dfs.prototype.walk = function ()
{
   var v_stack = new Array();
   var i, j;
   var cid = 0;
   var component = 0;

   while (true)
   {
      if (v_stack.length < 1)
      {
         var selected = -1;

         var findFunc = function (aid)
         {
            if (this.vertices[aid].dfs_state == 0)
            {
                selected = aid;
                return true;
            }
            return false;
         };

         while (cid < this.components.length && selected == -1) {
             selected = util.Set.find(this.components[cid], findFunc, this);
             if (selected === null) {
                 selected = -1;
                 cid++;
                 if (cid == this.nReactants) {
                     this.nComponentsInReactants = component;
                 }
             }
         }
         if (selected < -1) {
            this.molecule.atoms.find(findFunc, this);
         }
         if (selected == -1)
            break;
         this.vertices[selected].parent_vertex = -1;
         this.vertices[selected].parent_edge = -1;
         v_stack.push(selected);
         component++;
      }

      var v_idx = v_stack.pop();
      var parent_vertex = this.vertices[v_idx].parent_vertex;

      var seq_elem = new chem.Dfs.SeqElem(v_idx, parent_vertex, this.vertices[v_idx].parent_edge);
      this.v_seq.push(seq_elem);

      this.vertices[v_idx].dfs_state = 2;

      var atom_d = this.atom_data[v_idx];

      for (i = 0; i < atom_d.neighbours.length; i++)
      {
         var nei_idx = atom_d.neighbours[i].aid;
         var edge_idx = atom_d.neighbours[i].bid;

         if (nei_idx == parent_vertex)
            continue;

         if (this.vertices[nei_idx].dfs_state == 2)
         {
            this.edges[edge_idx].closing_cycle = 1;

            j = v_idx;

            while (j != -1)
            {
               if (this.vertices[j].parent_vertex == nei_idx)
                  break;
               j = this.vertices[j].parent_vertex;
            }

            if (j == -1)
               throw new Error("cycle unwind error");

            this.edges[this.vertices[j].parent_edge].opening_cycles++;
            this.vertices[v_idx].branches++;

            seq_elem = new chem.Dfs.SeqElem(nei_idx, v_idx, edge_idx);
            this.v_seq.push(seq_elem);
         }
         else
         {
            if (this.vertices[nei_idx].dfs_state == 1)
            {
               j = v_stack.indexOf(nei_idx);

               if (j == -1)
                  throw new Error("internal: removing vertex from stack");

               v_stack.splice(j, 1);

               var parent = this.vertices[nei_idx].parent_vertex;

               if (parent >= 0)
                  this.vertices[parent].branches--;
            }

            this.vertices[v_idx].branches++;
            this.vertices[nei_idx].parent_vertex = v_idx;
            this.vertices[nei_idx].parent_edge = edge_idx;
            this.vertices[nei_idx].dfs_state = 1;
            v_stack.push(nei_idx);
         }
      }
   }
};

chem.Dfs.prototype.edgeClosingCycle = function (e_idx)
{
   return this.edges[e_idx].closing_cycle != 0;
};

chem.Dfs.prototype.numBranches = function (v_idx)
{
   return this.vertices[v_idx].branches;
};

chem.Dfs.prototype.numOpeningCycles = function (e_idx)
{
   return this.edges[e_idx].opening_cycles;
};

chem.Dfs.prototype.toString = function ()
{
    var str = '';
    this.v_seq.each(function (seq_elem) {str += seq_elem.idx + ' -> '});
    str += '*';
    return str;
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.chem || !chem.Struct)
    throw new Error("Vec2 and Molecule should be defined first");
    
chem.CisTrans = function (mol, neighbors_func, context)
{
    this.molecule = mol;
    this.bonds = new util.Map();
    this.getNeighbors = neighbors_func;
    this.context = context;
};

chem.CisTrans.PARITY =
{
    NONE:  0,
    CIS:   1,
    TRANS: 2
};

chem.CisTrans.prototype.each = function (func, context)
{
    this.bonds.each(func, context);
};

chem.CisTrans.prototype.getParity = function (idx)
{
    return this.bonds.get(idx).parity;
};

chem.CisTrans.prototype.getSubstituents = function (idx)
{
    return this.bonds.get(idx).substituents;
};

chem.CisTrans.prototype.sameside = function (beg, end, nei_beg, nei_end)
{
   var diff = util.Vec2.diff(beg, end);
   var norm = new util.Vec2(-diff.y, diff.x);

   if (!norm.normalize())
      return 0;

   var norm_beg = util.Vec2.diff(nei_beg, beg);
   var norm_end = util.Vec2.diff(nei_end, end);

   if (!norm_beg.normalize())
      return 0;
   if (!norm_end.normalize())
      return 0;

   var prod_beg = util.Vec2.dot(norm_beg, norm);
   var prod_end = util.Vec2.dot(norm_end, norm);

   if (Math.abs(prod_beg) < 0.001 || Math.abs(prod_end) < 0.001)
      return 0;

   return (prod_beg * prod_end > 0) ? 1 : -1;
};

chem.CisTrans.prototype._sameside = function (i_beg, i_end, i_nei_beg, i_nei_end)
{
   return this.sameside(this.molecule.atoms.get(i_beg).pp, this.molecule.atoms.get(i_end).pp,
      this.molecule.atoms.get(i_nei_beg).pp, this.molecule.atoms.get(i_nei_end).pp);
};

chem.CisTrans.prototype._sortSubstituents = function (substituents)
{
   var h0 = this.molecule.atoms.get(substituents[0]).pureHydrogen();
   var h1 = substituents[1] < 0 || this.molecule.atoms.get(substituents[1]).pureHydrogen();
   var h2 = this.molecule.atoms.get(substituents[2]).pureHydrogen();
   var h3 = substituents[3] < 0 || this.molecule.atoms.get(substituents[3]).pureHydrogen();

   if (h0 && h1)
      return false;
   if (h2 && h3)
      return false;

   if (h1)
      substituents[1] = -1;
   else if (h0)
   {
      substituents[0] = substituents[1];
      substituents[1] = -1;
   }
   else if (substituents[0] > substituents[1])
      substituents.swap(0, 1);

   if (h3)
      substituents[3] = -1;
   else if (h2)
   {
      substituents[2] = substituents[3];
      substituents[3] = -1;
   }
   else if (substituents[2] > substituents[3])
      substituents.swap(2, 3);

   return true;
};

chem.CisTrans.prototype.isGeomStereoBond = function (bond_idx, substituents)
{
   // it must be [C,N,Si]=[C,N,Si] bond
   
   var bond = this.molecule.bonds.get(bond_idx);

   if (bond.type != chem.Struct.BOND.TYPE.DOUBLE)
      return false;

   var label1 = this.molecule.atoms.get(bond.begin).label;
   var label2 = this.molecule.atoms.get(bond.end).label;

   if (label1 != 'C' && label1 != 'N' && label1 != 'Si' && label1 != 'Ge')
      return false;
   if (label2 != 'C' && label2 != 'N' && label2 != 'Si' && label2 != 'Ge')
      return false;

   // the atoms should have 1 or 2 single bonds
   // (apart from the double bond under consideration)
   var nei_begin = this.getNeighbors.call(this.context, bond.begin);
   var nei_end = this.getNeighbors.call(this.context, bond.end);

   if (nei_begin.length < 2 || nei_begin.length > 3 ||
       nei_end.length < 2 || nei_end.length > 3)
      return false;

   substituents[0] = -1;
   substituents[1] = -1;
   substituents[2] = -1;
   substituents[3] = -1;

   var i;
   var nei;
   
   for (i = 0; i < nei_begin.length; i++)
   {
      nei = nei_begin[i];
      
      if (nei.bid == bond_idx)
         continue;
      
      if (this.molecule.bonds.get(nei.bid).type != chem.Struct.BOND.TYPE.SINGLE)
         return false;

      if (substituents[0] == -1)
         substituents[0] = nei.aid;
      else // (substituents[1] == -1)
         substituents[1] = nei.aid;
   }

   for (i = 0; i < nei_end.length; i++)
   {
      nei = nei_end[i];

      if (nei.bid == bond_idx)
         continue;
      
      if (this.molecule.bonds.get(nei.bid).type != chem.Struct.BOND.TYPE.SINGLE)
         return false;

      if (substituents[2] == -1)
         substituents[2] = nei.aid;
      else // (substituents[3] == -1)
         substituents[3] = nei.aid;
   }

   if (substituents[1] != -1 && this._sameside(bond.begin, bond.end, substituents[0], substituents[1]) != -1)
      return false;
   if (substituents[3] != -1 && this._sameside(bond.begin, bond.end, substituents[2], substituents[3]) != -1)
      return false;

   return true;
};

chem.CisTrans.prototype.build = function (exclude_bonds)
{
   this.molecule.bonds.each(function (bid, bond)
   {
      var ct = this.bonds.set(bid, 
      { 
         parity: 0,
         substituents: new Array(4)
      });

      if (Object.isArray(exclude_bonds) && exclude_bonds[bid])
         return;

      if (!this.isGeomStereoBond(bid, ct.substituents))
         return;

      if (!this._sortSubstituents(ct.substituents))
         return;

      var sign = this._sameside(bond.begin, bond.end, ct.substituents[0], ct.substituents[2]);

      if (sign == 1)
         ct.parity = chem.CisTrans.PARITY.CIS;
      else if (sign == -1)
         ct.parity = chem.CisTrans.PARITY.TRANS;
   }, this);
};


    
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.chem || !chem.Struct)
    throw new Error("Vec2 and Molecule should be defined first");
    
chem.Stereocenters = function (mol, neighbors_func, context)
{
    this.molecule = mol;
    this.atoms = new util.Map();
    this.getNeighbors = neighbors_func;
    this.context = context;
};

chem.Stereocenters.prototype.each = function (func, context)
{
    this.atoms.each(func, context);
};

chem.Stereocenters.prototype.buildFromBonds = function (/*const int *atom_types, const int *atom_groups, const int *bond_orientations, */ignore_errors)
{
   //_bond_directions.copy(bond_orientations, mol.edgeEnd());

   this.molecule.atoms.each(function (aid)
   {
      /*
      if (atom_types[atom_idx] == 0)
         continue;
         */
      var nei_list = this.getNeighbors.call(this.context, aid);
      var stereocenter = false;
      
      nei_list.find(function (nei)
      {
         var bond = this.molecule.bonds.get(nei.bid);
         
         if (bond.type == chem.Struct.BOND.TYPE.SINGLE && bond.begin == aid)
            if (bond.stereo == chem.Struct.BOND.STEREO.UP || bond.stereo == chem.Struct.BOND.STEREO.DOWN)
            {
                stereocenter = true;
                return true;
            }
         return false;
      }, this);
      
      if (!stereocenter)
         return;

      if (ignore_errors)
      {
//         try
//         {
            this._buildOneCenter(aid/*, atom_groups[atom_idx], atom_types[atom_idx], bond_orientations*/);
//         }
//         catch (er)
//         {
//         }
      }
      else
         this._buildOneCenter(aid/*, atom_groups[atom_idx], atom_types[atom_idx], bond_orientations*/);
   }, this);
};

chem.Stereocenters.allowed_stereocenters =
[
    {elem: 'C',  charge: 0, degree: 3, n_double_bonds: 0, implicit_degree: 4},
    {elem: 'C',  charge: 0, degree: 4, n_double_bonds: 0, implicit_degree: 4},
    {elem: 'Si', charge: 0, degree: 3, n_double_bonds: 0, implicit_degree: 4},
    {elem: 'Si', charge: 0, degree: 4, n_double_bonds: 0, implicit_degree: 4},
    {elem: 'N',  charge: 1, degree: 3, n_double_bonds: 0, implicit_degree: 4},
    {elem: 'N',  charge: 1, degree: 4, n_double_bonds: 0, implicit_degree: 4},
    {elem: 'N',  charge: 0, degree: 3, n_double_bonds: 0, implicit_degree: 3},
    {elem: 'S',  charge: 0, degree: 4, n_double_bonds: 2, implicit_degree: 4},
    {elem: 'S',  charge: 1, degree: 3, n_double_bonds: 0, implicit_degree: 3},
    {elem: 'S',  charge: 0, degree: 3, n_double_bonds: 1, implicit_degree: 3},
    {elem: 'P',  charge: 0, degree: 3, n_double_bonds: 0, implicit_degree: 3},
    {elem: 'P',  charge: 1, degree: 4, n_double_bonds: 0, implicit_degree: 4},
    {elem: 'P',  charge: 0, degree: 4, n_double_bonds: 1, implicit_degree: 4}
];


chem.Stereocenters.prototype._buildOneCenter = function (atom_idx/*, int group, int type, const int *bond_orientations*/)
{
   var atom = this.molecule.atoms.get(atom_idx);

   var nei_list = this.getNeighbors.call(this.context, atom_idx);
   var degree = nei_list.length;
   var implicit_degree = -1;

   var stereocenter =
   {
       group: 0, // = group;
       type: 0, // = type;
       pyramid: new Array(4)
   };
   
   var nei_idx = 0;
   var edge_ids = new Array(4);

   var last_atom_dir = 0;
   var n_double_bonds = 0;

   stereocenter.pyramid[0] = -1;
   stereocenter.pyramid[1] = -1;
   stereocenter.pyramid[2] = -1;
   stereocenter.pyramid[3] = -1;

   var n_pure_hydrogens = 0;

   if (degree > 4)
      throw new Error("stereocenter with %d bonds are not supported" + degree);

   nei_list.each(function (nei)
   {
      var nei_atom = this.molecule.atoms.get(nei.aid);
      var bond = this.molecule.bonds.get(nei.bid);

      edge_ids[nei_idx] =
      {
         edge_idx: nei.bid,
         nei_idx: nei.aid,
         rank: nei.aid,
         vec: util.Vec2.diff(nei_atom.pp, atom.pp)
      };

      if (nei_atom.pureHydrogen())
      {
         n_pure_hydrogens++;
         edge_ids[nei_idx].rank = 10000;
      } else if (nei_atom.label == 'H')
         edge_ids[nei_idx].rank = 5000;

      if (!edge_ids[nei_idx].vec.normalize())
         throw new Error("zero bond length");

      if (bond.type == chem.Struct.BOND.TYPE.TRIPLE)
         throw new Error("non-single bonds not allowed near stereocenter");
      else if (bond.type == chem.Struct.BOND.TYPE.AROMATIC)
         throw new Error("aromatic bonds not allowed near stereocenter");
      else if (bond.type == chem.Struct.BOND.TYPE.DOUBLE)
         n_double_bonds++;

      nei_idx++;
   }, this);

   chem.Stereocenters.allowed_stereocenters.find(function (as)
   {
      if (as.elem == atom.label && as.charge == atom.charge &&
          as.degree == degree && as.n_double_bonds == n_double_bonds)
      {
         implicit_degree = as.implicit_degree;
         return true;
      }
      return false;
   }, this);

   if (implicit_degree == -1)
      throw new Error("unknown stereocenter configuration: " + atom.label + ", charge " + atom.charge + ", " + degree + " bonds (" + n_double_bonds + " double)");

   if (degree == 4 && n_pure_hydrogens > 1)
      throw new Error(n_pure_hydrogens + " hydrogens near stereocenter");

   if (degree == 3 && implicit_degree == 4 && n_pure_hydrogens > 0)
      throw new Error("have hydrogen(s) besides implicit hydrogen near stereocenter");

   /*
   if (stereocenter.type == ATOM_ANY)
   {
      _stereocenters.insert(atom_idx, stereocenter);
      return;
   }
   */

   if (degree == 4)
   {
      // sort by neighbor atom index (ascending)
      if (edge_ids[0].rank > edge_ids[1].rank)
         edge_ids.swap(0, 1);
      if (edge_ids[1].rank > edge_ids[2].rank)
         edge_ids.swap(1, 2);
      if (edge_ids[2].rank > edge_ids[3].rank)
         edge_ids.swap(2, 3);
      if (edge_ids[1].rank > edge_ids[2].rank)
         edge_ids.swap(1, 2);
      if (edge_ids[0].rank > edge_ids[1].rank)
         edge_ids.swap(0, 1);
      if (edge_ids[1].rank > edge_ids[2].rank)
         edge_ids.swap(1, 2);

      var main1 = -1, main2 = -1, side1 = -1, side2 = -1;
      var main_dir = 0;

      for (nei_idx = 0; nei_idx < 4; nei_idx++)
      {
         var stereo = this._getBondStereo(atom_idx, edge_ids[nei_idx].edge_idx);

         if (stereo == chem.Struct.BOND.STEREO.UP || stereo == chem.Struct.BOND.STEREO.DOWN)
         {
            main1 = nei_idx;
            main_dir = stereo;
            break;
         }
      }

      if (main1 == -1)
         throw new Error("none of 4 bonds going from stereocenter is stereobond");

      var xyz1, xyz2;

      // find main2 as opposite to main1
      if (main2 == -1)
      {
         xyz1 = chem.Stereocenters._xyzzy(edge_ids[main1].vec, edge_ids[(main1 + 1) % 4].vec, edge_ids[(main1 + 2) % 4].vec);
         xyz2 = chem.Stereocenters._xyzzy(edge_ids[main1].vec, edge_ids[(main1 + 1) % 4].vec, edge_ids[(main1 + 3) % 4].vec);

         if (xyz1 + xyz2 == 3 || xyz1 + xyz2 == 12)
         {
            main2 = (main1 + 1) % 4;
            side1 = (main1 + 2) % 4;
            side2 = (main1 + 3) % 4;
         }
      }
      if (main2 == -1)
      {
         xyz1 = chem.Stereocenters._xyzzy(edge_ids[main1].vec, edge_ids[(main1 + 2) % 4].vec, edge_ids[(main1 + 1) % 4].vec);
         xyz2 = chem.Stereocenters._xyzzy(edge_ids[main1].vec, edge_ids[(main1 + 2) % 4].vec, edge_ids[(main1 + 3) % 4].vec);

         if (xyz1 + xyz2 == 3 || xyz1 + xyz2 == 12)
         {
            main2 = (main1 + 2) % 4;
            side1 = (main1 + 1) % 4;
            side2 = (main1 + 3) % 4;
         }
      }
      if (main2 == -1)
      {
         xyz1 = chem.Stereocenters._xyzzy(edge_ids[main1].vec, edge_ids[(main1 + 3) % 4].vec, edge_ids[(main1 + 1) % 4].vec);
         xyz2 = chem.Stereocenters._xyzzy(edge_ids[main1].vec, edge_ids[(main1 + 3) % 4].vec, edge_ids[(main1 + 2) % 4].vec);

         if (xyz1 + xyz2 == 3 || xyz1 + xyz2 == 12)
         {
            main2 = (main1 + 3) % 4;
            side1 = (main1 + 2) % 4;
            side2 = (main1 + 1) % 4;
         }
      }

      if (main2 == -1)
         throw new Error("internal error: can not find opposite bond");

      if (main_dir == chem.Struct.BOND.STEREO.UP && this._getBondStereo(atom_idx, edge_ids[main2].edge_idx) == chem.Struct.BOND.STEREO.DOWN)
         throw new Error("stereo types of the opposite bonds mismatch");
      if (main_dir == chem.Struct.BOND.STEREO.DOWN && this._getBondStereo(atom_idx, edge_ids[main2].edge_idx) == chem.Struct.BOND.STEREO.UP)
         throw new Error("stereo types of the opposite bonds mismatch");

      if (main_dir == this._getBondStereo(atom_idx, edge_ids[side1].edge_idx))
         throw new Error("stereo types of non-opposite bonds match");
      if (main_dir == this._getBondStereo(atom_idx, edge_ids[side2].edge_idx))
         throw new Error("stereo types of non-opposite bonds match");

      if (main1 == 3 || main2 == 3)
         last_atom_dir = main_dir;
      else
         last_atom_dir = (main_dir == chem.Struct.BOND.STEREO.UP ? chem.Struct.BOND.STEREO.DOWN : chem.Struct.BOND.STEREO.UP);

      sign = chem.Stereocenters._sign(edge_ids[0].vec, edge_ids[1].vec, edge_ids[2].vec);

      if ((last_atom_dir == chem.Struct.BOND.STEREO.UP && sign > 0) ||
          (last_atom_dir == chem.Struct.BOND.STEREO.DOWN && sign < 0))
      {
         stereocenter.pyramid[0] = edge_ids[0].nei_idx;
         stereocenter.pyramid[1] = edge_ids[1].nei_idx;
         stereocenter.pyramid[2] = edge_ids[2].nei_idx;
      }
      else
      {
         stereocenter.pyramid[0] = edge_ids[0].nei_idx;
         stereocenter.pyramid[1] = edge_ids[2].nei_idx;
         stereocenter.pyramid[2] = edge_ids[1].nei_idx;
      }

      stereocenter.pyramid[3] = edge_ids[3].nei_idx;
   }
   else if (degree == 3)
   {
      // sort by neighbor atom index (ascending)
      if (edge_ids[0].rank > edge_ids[1].rank)
         edge_ids.swap(0, 1);
      if (edge_ids[1].rank > edge_ids[2].rank)
         edge_ids.swap(1, 2);
      if (edge_ids[0].rank > edge_ids[1].rank)
         edge_ids.swap(0, 1);

      var stereo0 = this._getBondStereo(atom_idx, edge_ids[0].edge_idx);
      var stereo1 = this._getBondStereo(atom_idx, edge_ids[1].edge_idx);
      var stereo2 = this._getBondStereo(atom_idx, edge_ids[2].edge_idx);

      var n_up = 0, n_down = 0;

      n_up += ((stereo0 == chem.Struct.BOND.STEREO.UP) ? 1 : 0);
      n_up += ((stereo1 == chem.Struct.BOND.STEREO.UP) ? 1 : 0);
      n_up += ((stereo2 == chem.Struct.BOND.STEREO.UP) ? 1 : 0);

      n_down += ((stereo0 == chem.Struct.BOND.STEREO.DOWN) ? 1 : 0);
      n_down += ((stereo1 == chem.Struct.BOND.STEREO.DOWN) ? 1 : 0);
      n_down += ((stereo2 == chem.Struct.BOND.STEREO.DOWN) ? 1 : 0);

      if (implicit_degree == 4) // have implicit hydrogen
      {
         if (n_up == 3)
            throw new Error("all 3 bonds up near stereoatom");
         if (n_down == 3)
            throw new Error("all 3 bonds down near stereoatom");

         if (n_up == 0 && n_down == 0)
            throw new Error("no up/down bonds near stereoatom -- indefinite case");
         if (n_up == 1 && n_down == 1)
            throw new Error("one bond up, one bond down -- indefinite case");

         main_dir = 0;

         if (n_up == 2)
            last_atom_dir = chem.Struct.BOND.STEREO.DOWN;
         else if (n_down == 2)
            last_atom_dir = chem.Struct.BOND.STEREO.UP;
         else
         {
            main1 = -1;
            side1 = -1;
            side2 = -1;

            for (nei_idx = 0; nei_idx < 3; nei_idx++)
            {
               dir = this._getBondStereo(atom_idx, edge_ids[nei_idx].edge_idx);

               if (dir == chem.Struct.BOND.STEREO.UP || dir == chem.Struct.BOND.STEREO.DOWN)
               {
                  main1 = nei_idx;
                  main_dir = dir;
                  side1 = (nei_idx + 1) % 3;
                  side2 = (nei_idx + 2) % 3;
                  break;
               }
            }

            if (main1 == -1)
               throw new Error("internal error: can not find up or down bond");

            var xyz = chem.Stereocenters._xyzzy(edge_ids[side1].vec, edge_ids[side2].vec, edge_ids[main1].vec);

            if (xyz == 3 || xyz == 4)
               throw new Error("degenerate case for 3 bonds near stereoatom");

            if (xyz == 1)
               last_atom_dir = main_dir;
            else
               last_atom_dir = (main_dir == chem.Struct.BOND.STEREO.UP ? chem.Struct.BOND.STEREO.DOWN : chem.Struct.BOND.STEREO.UP);
         }

         var sign = chem.Stereocenters._sign(edge_ids[0].vec, edge_ids[1].vec, edge_ids[2].vec);

         if ((last_atom_dir == chem.Struct.BOND.STEREO.UP && sign > 0) ||
             (last_atom_dir == chem.Struct.BOND.STEREO.DOWN && sign < 0))
         {
            stereocenter.pyramid[0] = edge_ids[0].nei_idx;
            stereocenter.pyramid[1] = edge_ids[1].nei_idx;
            stereocenter.pyramid[2] = edge_ids[2].nei_idx;
         }
         else
         {
            stereocenter.pyramid[0] = edge_ids[0].nei_idx;
            stereocenter.pyramid[1] = edge_ids[2].nei_idx;
            stereocenter.pyramid[2] = edge_ids[1].nei_idx;
         }

         stereocenter.pyramid[3] = -1;
      }
      else // 3-connected P, N or S; no implicit hydrogens
      {
         var dir;

         if (n_down > 0 && n_up > 0)
            throw new Error("one bond up, one bond down -- indefinite case");
         else if (n_down == 0 && n_up == 0)
            throw new Error("no up-down bonds attached to stereocenter");
         else if (n_up > 0)
            dir = 1;
         else
            dir = -1;

         if (chem.Stereocenters._xyzzy(edge_ids[0].vec, edge_ids[1].vec, edge_ids[2].vec) == 1 ||
             chem.Stereocenters._xyzzy(edge_ids[0].vec, edge_ids[2].vec, edge_ids[1].vec) == 1 ||
             chem.Stereocenters._xyzzy(edge_ids[2].vec, edge_ids[1].vec, edge_ids[0].vec) == 1)
            // all bonds belong to the same half-plane
            dir = -dir;

         sign = chem.Stereocenters._sign(edge_ids[0].vec, edge_ids[1].vec, edge_ids[2].vec);

         if (sign == dir)
         {
            stereocenter.pyramid[0] = edge_ids[0].nei_idx;
            stereocenter.pyramid[1] = edge_ids[2].nei_idx;
            stereocenter.pyramid[2] = edge_ids[1].nei_idx;
         }
         else
         {
            stereocenter.pyramid[0] = edge_ids[0].nei_idx;
            stereocenter.pyramid[1] = edge_ids[1].nei_idx;
            stereocenter.pyramid[2] = edge_ids[2].nei_idx;
         }
         stereocenter.pyramid[3] = -1;
      }
   }

   this.atoms.set(atom_idx, stereocenter);
};

chem.Stereocenters.prototype._getBondStereo = function (center_idx, edge_idx)
{
   var bond = this.molecule.bonds.get(edge_idx);

   if (center_idx != bond.begin) // TODO: check this
      return 0;

   return bond.stereo;
};

// 1 -- in the smaller angle, 2 -- in the bigger angle,
// 4 -- in the 'positive' straight angle, 8 -- in the 'negative' straight angle
chem.Stereocenters._xyzzy = function (v1, v2, u)
{
   var eps = 0.001;

   var sine1 = util.Vec2.cross(v1, v2);
   var cosine1 = util.Vec2.dot(v1, v2);

   var sine2 = util.Vec2.cross(v1, u);
   var cosine2 = util.Vec2.dot(v1, u);

   if (Math.abs(sine1) < eps)
   {
      if (Math.abs(sine2) < eps)
         throw new Error("degenerate case -- bonds overlap");

      return (sine2 > 0) ? 4 : 8;
   }

   if (sine1 * sine2 < -eps * eps)
      return 2;

   if (cosine2 < cosine1)
      return 2;

   return 1;
};

chem.Stereocenters._sign = function (v1, v2, v3)
{
   var res = (v1.x - v3.x) * (v2.y - v3.y) - (v1.y - v3.y) * (v2.x - v3.x);
   var eps = 0.001;

   if (res > eps)
      return 1;
   if (res < -eps)
      return -1;

   throw new Error("degenerate triangle");
};

chem.Stereocenters.isPyramidMappingRigid = function (mapping)
{
   var arr = mapping.clone();
   var rigid = true;

   if (arr[0] > arr[1])
      arr.swap(0, 1), rigid = !rigid;
   if (arr[1] > arr[2])
      arr.swap(1, 2), rigid = !rigid;
   if (arr[2] > arr[3])
      arr.swap(2, 3), rigid = !rigid;
   if (arr[1] > arr[2])
      arr.swap(1, 2), rigid = !rigid;
   if (arr[0] > arr[1])
      arr.swap(0, 1), rigid = !rigid;
   if (arr[1] > arr[2])
      arr.swap(1, 2), rigid = !rigid;

   return rigid;
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.chem || !chem.Struct)
    throw new Error("Vec2 and Molecule should be defined first");

chem.SmilesSaver = function (render)
{
    this.smiles = '';
    this._written_atoms = new Array();
    this._written_components = 0;

    if (Object.isUndefined(render))
    {
        if (Object.isUndefined(ui.render))
            throw new Error ("Render object is undefined");
        this._render = ui.render;
    } else
        this._render = render;

    this.ignore_errors = false;
};

chem.SmilesSaver._Atom = function (h_count)
{
    this.neighbours = new Array();  // Array of integer pairs {a, b}
    this.aromatic = false;          // has aromatic bond
    this.lowercase = false;         // aromatic and has to be written lowercase
    this.chirality = 0;             // 0 means no chirality, 1 means CCW pyramid, 2 means CW pyramid
    this.branch_cnt = 0;            // runs from 0 to (branches - 1)
    this.paren_written = false;
    this.h_count = h_count;
    this.parent = -1;
};

chem.SmilesSaver.prototype.saveMolecule = function (molecule, ignore_errors)
{
    var i, j, k;

    if (!Object.isUndefined(ignore_errors))
        this.ignore_errors = ignore_errors;

    if (molecule.sgroups.count() > 0 && !this.ignore_errors)
        throw new Error("SMILES doesn't support s-groups");

    this.atoms = new Array(molecule.atoms.count());

    molecule.atoms.each(function (aid, atom)
    {
        this.atoms[aid] = new chem.SmilesSaver._Atom(atom.implicitH);
    }, this);

     // From the SMILES specification:
     // Please note that only atoms on the following list 
     // can be considered aromatic: C, N, O, P, S, As, Se, and * (wildcard).
     var allowed_lowercase = ['B', 'C', 'N', 'O', 'P', 'S', 'Se', 'As'];

    // Detect atoms that have aromatic bonds and count neighbours
    molecule.bonds.each(function (bid, bond)
    {
        if (bond.type == chem.Struct.BOND.TYPE.AROMATIC)
        {
           this.atoms[bond.begin].aromatic = true;
           if (allowed_lowercase.indexOf(molecule.atoms.get(bond.begin).label) != -1)
               this.atoms[bond.begin].lowercase = true;
           this.atoms[bond.end].aromatic = true;
           if (allowed_lowercase.indexOf(molecule.atoms.get(bond.end).label) != -1)
               this.atoms[bond.end].lowercase = true;
        }
        this.atoms[bond.begin].neighbours.push({aid: bond.end, bid: bid});
        this.atoms[bond.end].neighbours.push({aid: bond.begin, bid: bid});
    }, this);

    this._touched_cistransbonds = 0;
    this._markCisTrans(molecule);

    var components = chem.MolfileSaver.getComponents(molecule);
    var componentsAll = components.reactants.concat(components.products);

    var walk = new chem.Dfs(molecule, this.atoms, componentsAll, components.reactants.length);

    walk.walk();

    this.atoms.each(function (atom)
    {
        atom.neighbours.clear();
    }, this);

    // fill up neighbor lists for the stereocenters calculation
    for (i = 0; i < walk.v_seq.length; i++)
    {
        var seq_el = walk.v_seq[i];
        var v_idx = seq_el.idx;
        var e_idx = seq_el.parent_edge;
        var v_prev_idx = seq_el.parent_vertex;

        if (e_idx >= 0)
        {
            var atom = this.atoms[v_idx];

            var opening_cycles = walk.numOpeningCycles(e_idx);

            for (j = 0; j < opening_cycles; j++)
                this.atoms[v_prev_idx].neighbours.push({aid: -1, bid: -1});

            if (walk.edgeClosingCycle(e_idx))
            {
                for (k = 0; k < atom.neighbours.length; k++)
                {
                    if (atom.neighbours[k].aid == -1)
                    {
                        atom.neighbours[k].aid = v_prev_idx;
                        atom.neighbours[k].bid = e_idx;
                        break;
                    }
                }
                if (k == atom.neighbours.length)
                    throw new Error("internal: can not put closing bond to its place");
            }
            else
            {
               atom.neighbours.push({aid: v_prev_idx, bid: e_idx});
               atom.parent = v_prev_idx;
            }
            this.atoms[v_prev_idx].neighbours.push({aid: v_idx, bid: e_idx});
        }
    }

    // detect chiral configurations
    var stereocenters = new chem.Stereocenters(molecule, function (idx)
    {
       return this.atoms[idx].neighbours;
    }, this);
    stereocenters.buildFromBonds(this.ignore_errors);

    stereocenters.each (function (atom_idx, sc)
    {
        //if (sc.type < MoleculeStereocenters::ATOM_AND)
        //    continue;

        var implicit_h_idx = -1;

        if (sc.pyramid[3] == -1)
            implicit_h_idx = 3;
            /*
        else for (j = 0; j < 4; j++)
            if (ignored_vertices[pyramid[j]])
            {
                implicit_h_idx = j;
                break;
            }
            */

        var pyramid_mapping = new Array(4);
        var counter = 0;

        var atom = this.atoms[atom_idx];

        if (atom.parent != -1)
            for (k = 0; k < 4; k++)
                if (sc.pyramid[k] == atom.parent)
                {
                    pyramid_mapping[counter++] = k;
                    break;
                }

        if (implicit_h_idx != -1)
            pyramid_mapping[counter++] = implicit_h_idx;

        for (j = 0; j != atom.neighbours.length; j++)
        {
            if (atom.neighbours[j].aid == atom.parent)
                continue;

            for (k = 0; k < 4; k++)
                if (atom.neighbours[j].aid == sc.pyramid[k])
                {
                    if (counter >= 4)
                        throw new Error("internal: pyramid overflow");
                    pyramid_mapping[counter++] = k;
                    break;
             }
        }

        if (counter == 4)
        {
            // move the 'from' atom to the end
            counter = pyramid_mapping[0];
            pyramid_mapping[0] = pyramid_mapping[1];
            pyramid_mapping[1] = pyramid_mapping[2];
            pyramid_mapping[2] = pyramid_mapping[3];
            pyramid_mapping[3] = counter;
        }
        else if (counter != 3)
            throw new Error("cannot calculate chirality");

        if (chem.Stereocenters.isPyramidMappingRigid(pyramid_mapping))
            this.atoms[atom_idx].chirality = 1;
        else
            this.atoms[atom_idx].chirality = 2;
    }, this);

    // write the SMILES itself

    // cycle_numbers[i] == -1 means that the number is available
    // cycle_numbers[i] == n means that the number is used by vertex n
    var cycle_numbers = new Array();

    cycle_numbers.push(0); // never used

    var first_component = true;

    for (i = 0; i < walk.v_seq.length; i++)
    {
        seq_el = walk.v_seq[i];
        v_idx = seq_el.idx;
        e_idx = seq_el.parent_edge;
        v_prev_idx = seq_el.parent_vertex;
        var write_atom = true;

        if (v_prev_idx >= 0)
        {
            if (walk.numBranches(v_prev_idx) > 1)
                if (this.atoms[v_prev_idx].branch_cnt > 0 && this.atoms[v_prev_idx].paren_written)
                    this.smiles += ')';

            opening_cycles = walk.numOpeningCycles(e_idx);

            for (j = 0; j < opening_cycles; j++)
            {
                for (k = 1; k < cycle_numbers.length; k++)
                    if (cycle_numbers[k] == -1)
                       break;
                if (k == cycle_numbers.length)
                    cycle_numbers.push(v_prev_idx);
                else
                    cycle_numbers[k] = v_prev_idx;

                this._writeCycleNumber(k);
            }

            if (v_prev_idx >= 0)
            {
                var branches = walk.numBranches(v_prev_idx);

                if (branches > 1)
                    if (this.atoms[v_prev_idx].branch_cnt < branches - 1)
                    {
                        if (walk.edgeClosingCycle(e_idx))
                            this.atoms[v_prev_idx].paren_written = false;
                        else
                        {
                            this.smiles += '(';
                            this.atoms[v_prev_idx].paren_written = true;
                        }
                    }

                this.atoms[v_prev_idx].branch_cnt++;

                if (this.atoms[v_prev_idx].branch_cnt > branches)
                    throw new Error("unexpected branch");
            }

            var bond = molecule.bonds.get(e_idx);
            var bond_written = true;

            var dir = 0;

            if (bond.type == chem.Struct.BOND.TYPE.SINGLE)
               dir = this._calcBondDirection(molecule, e_idx, v_prev_idx);

            if ((dir == 1 && v_idx == bond.end) || (dir == 2 && v_idx == bond.begin))
                this.smiles += '/';
            else if ((dir == 2 && v_idx == bond.end) || (dir == 1 && v_idx == bond.begin))
                this.smiles += '\\';
            else if (bond.type == chem.Struct.BOND.TYPE.ANY)
                this.smiles += '~';
            else if (bond.type == chem.Struct.BOND.TYPE.DOUBLE)
                this.smiles += '=';
            else if (bond.type == chem.Struct.BOND.TYPE.TRIPLE)
                this.smiles += '#';
            else if (bond.type == chem.Struct.BOND.TYPE.AROMATIC && (!this.atoms[bond.begin].lowercase || !this.atoms[bond.end].lowercase))
                this.smiles += ':'; // TODO: Check if this : is needed
            else if (bond.type == chem.Struct.BOND.TYPE.SINGLE && this.atoms[bond.begin].aromatic && this.atoms[bond.end].aromatic)
                this.smiles += '-';
            else
                bond_written = false;


            if (walk.edgeClosingCycle(e_idx))
            {
                for (j = 1; j < cycle_numbers.length; j++)
                    if (cycle_numbers[j] == v_idx)
                        break;

                if (j == cycle_numbers.length)
                    throw new Error("cycle number not found");

                this._writeCycleNumber(j);

                cycle_numbers[j] = -1;
                write_atom = false;
            }
        }
        else
        {
            if (!first_component)
                this.smiles += (this._written_components == walk.nComponentsInReactants) ? '>>' : '.';
            first_component = false;
            this._written_components++;
        }
        if (write_atom) {
            this._writeAtom(molecule, v_idx, this.atoms[v_idx].aromatic, this.atoms[v_idx].lowercase, this.atoms[v_idx].chirality);
            this._written_atoms.push(seq_el.idx);
        }
    }

    this.comma = false;

    //this._writeStereogroups(mol, atoms);
    this._writeRadicals(molecule);
    //this._writePseudoAtoms(mol);
    //this._writeHighlighting();

    if (this.comma)
        this.smiles += '|';

   return this.smiles;

};

chem.SmilesSaver.prototype._writeCycleNumber = function (n)
{
    if (n > 0 && n < 10)
        this.smiles += n;
    else if (n >= 10 && n < 100)
        this.smiles += '%' + n;
    else if (n >= 100 && n < 1000)
        this.smiles += '%%' + n;
    else
        throw new Error("bad cycle number: " + n);
};

chem.SmilesSaver.prototype._writeAtom = function (mol, idx, aromatic, lowercase, chirality)
{
    var atom = mol.atoms.get(idx);
    var i;
    var need_brackets = false;
    var hydro = -1;
    var aam = 0;

    /*
    if (mol.haveQueryAtoms())
    {
      query_atom = &mol.getQueryAtom(idx);

      if (query_atom->type == QUERY_ATOM_RGROUP)
      {
         if (mol.getRGroups()->isRGroupAtom(idx))
         {
            const Array<int> &rg = mol.getRGroups()->getSiteRGroups(idx);

            if (rg.size() != 1)
               throw Error("rgroup count %d", rg.size());

            _output.printf("[&%d]", rg[0] + 1);
         }
         else
            _output.printf("[&%d]", 1);

         return;
      }
    }
    */

    if (atom.label == 'A')
    {
        this.smiles += '*';
        return;
    }

    if (atom.label == 'R' || atom.label == 'R#')
    {
        this.smiles += '[*]';
        return;
    }

    if (this.atom_atom_mapping)
        aam = atom_atom_mapping[idx];

    if (atom.label != 'C' && atom.label != 'P' &&
       atom.label != 'N' && atom.label != 'S' &&
       atom.label != 'O' && atom.label != 'Cl' &&
       atom.label != 'F' && atom.label != 'Br' &&
       atom.label != 'B' && atom.label != 'I')
        need_brackets = true;

    if (atom.explicitValence || atom.radical != 0 || chirality > 0 ||
       (aromatic && atom.label != 'C' && atom.label != 'O') ||
       (aromatic && atom.label == 'C' && this.atoms[idx].neighbours.length < 3 && this.atoms[idx].h_count == 0))
        hydro = this.atoms[idx].h_count;

    if (chirality || atom.charge != 0 || atom.isotope > 0 || hydro >= 0 || aam > 0)
        need_brackets = true;

    if (need_brackets)
    {
        if (hydro == -1)
            hydro = this.atoms[idx].h_count;
        this.smiles += '[';
    }

    if (atom.isotope > 0)
        this.smiles += atom.isotope;

    if (lowercase)
        this.smiles += atom.label.toLowerCase();
    else
        this.smiles += atom.label;

    if (chirality > 0)
    {
        if (chirality == 1)
            this.smiles += '@';
        else // chirality == 2
            this.smiles += '@@';

        if (atom.implicitH > 1)
           throw new Error(atom.implicitH + " implicit H near stereocenter");
    }

    if (atom.label != 'H') {
        if (hydro > 1 || (hydro == 0 && !need_brackets))
            this.smiles += 'H' + hydro;
        else if (hydro == 1)
            this.smiles += 'H';
    }

    if (atom.charge > 1)
        this.smiles += '+' + atom.charge;
    else if (atom.charge < -1)
        this.smiles += atom.charge;
    else if (atom.charge == 1)
        this.smiles += '+';
    else if (atom.charge == -1)
        this.smiles += '-';

    if (aam > 0)
        this.smiles += ':' + aam;

    if (need_brackets)
        this.smiles += ']';

    /*
    if (mol.getRGroupFragment() != 0)
    {
      for (i = 0; i < 2; i++)
      {
         int j;

         for (j = 0; mol.getRGroupFragment()->getAttachmentPoint(i, j) != -1; j++)
            if (idx == mol.getRGroupFragment()->getAttachmentPoint(i, j))
            {
               _output.printf("([*])");
               break;
            }

         if (mol.getRGroupFragment()->getAttachmentPoint(i, j) != -1)
            break;
      }
    }
    */
};

chem.SmilesSaver.prototype._markCisTrans = function (mol)
{
   this.cis_trans = new chem.CisTrans (mol, function (idx)
   {
      return this.atoms[idx].neighbours;
   }, this);
   this.cis_trans.build();
   this._dbonds = new Array(mol.bonds.count());

   mol.bonds.each(function (bid)
   {
      this._dbonds[bid] =
      {
         ctbond_beg: -1,
         ctbond_end: -1,
         saved: 0
      }
   }, this);

   this.cis_trans.each(function (bid, ct)
   {
      var bond = mol.bonds.get(bid);

      if (ct.parity != 0 && !this._render.isBondInRing(bid))
      {
         var nei_beg = this.atoms[bond.begin].neighbours;
         var nei_end = this.atoms[bond.end].neighbours;
         var arom_fail_beg = true, arom_fail_end = true;

         nei_beg.each(function (nei)
         {
            if (nei.bid != bid && mol.bonds.get(nei.bid).type == chem.Struct.BOND.TYPE.SINGLE)
               arom_fail_beg = false;
         }, this);

         nei_end.each(function (nei)
         {
            if (nei.bid != bid && mol.bonds.get(nei.bid).type == chem.Struct.BOND.TYPE.SINGLE)
               arom_fail_end = false;
         }, this);

         if (arom_fail_beg || arom_fail_end)
            return;

         nei_beg.each(function (nei)
         {
            if (nei.bid != bid)
            {
               if (mol.bonds.get(nei.bid).begin == bond.begin)
                  this._dbonds[nei.bid].ctbond_beg = bid;
               else
                  this._dbonds[nei.bid].ctbond_end = bid;
            }
         }, this);

         nei_end.each(function (nei)
         {
            if (nei.bid != bid)
            {
               if (mol.bonds.get(nei.bid).begin == bond.end)
                  this._dbonds[nei.bid].ctbond_beg = bid;
               else
                  this._dbonds[nei.bid].ctbond_end = bid;
            }
         }, this);
      }
   }, this);
};

chem.SmilesSaver.prototype._updateSideBonds = function (mol, bond_idx)
{
   var bond = mol.bonds.get(bond_idx);
   var subst = this.cis_trans.getSubstituents(bond_idx);
   var parity = this.cis_trans.getParity(bond_idx);

   var sidebonds = [-1, -1, -1, -1];

   sidebonds[0] = mol.findBondId(subst[0], bond.begin);
   if (subst[1] != -1)
      sidebonds[1] = mol.findBondId(subst[1], bond.begin);

   sidebonds[2] = mol.findBondId(subst[2], bond.end);
   if (subst[3] != -1)
      sidebonds[3] = mol.findBondId(subst[3], bond.end);

   var n1 = 0, n2 = 0, n3 = 0, n4 = 0;

   if (this._dbonds[sidebonds[0]].saved != 0)
   {
      if ((this._dbonds[sidebonds[0]].saved == 1 && mol.bonds.get(sidebonds[0]).begin == bond.begin) ||
          (this._dbonds[sidebonds[0]].saved == 2 && mol.bonds.get(sidebonds[0]).end == bond.begin))
         n1++;
      else
         n2++;
   }
   if (sidebonds[1] != -1 && this._dbonds[sidebonds[1]].saved != 0)
   {
      if ((this._dbonds[sidebonds[1]].saved == 2 && mol.bonds.get(sidebonds[1]).begin == bond.begin) ||
          (this._dbonds[sidebonds[1]].saved == 1 && mol.bonds.get(sidebonds[1]).end == bond.begin))
         n1++;
      else
         n2++;
   }
   if (this._dbonds[sidebonds[2]].saved != 0)
   {
      if ((this._dbonds[sidebonds[2]].saved == 1 && mol.bonds.get(sidebonds[2]).begin == bond.end) ||
          (this._dbonds[sidebonds[2]].saved == 2 && mol.bonds.get(sidebonds[2]).end == bond.end))
         n3++;
      else
         n4++;
   }
   if (sidebonds[3] != -1 && this._dbonds[sidebonds[3]].saved != 0)
   {
      if ((this._dbonds[sidebonds[3]].saved == 2 && mol.bonds.get(sidebonds[3]).begin == bond.end) ||
          (this._dbonds[sidebonds[3]].saved == 1 && mol.bonds.get(sidebonds[3]).end == bond.end))
         n3++;
      else
         n4++;
   }

   if (parity == chem.CisTrans.PARITY.CIS)
   {
      n1 += n3;
      n2 += n4;
   }
   else
   {
      n1 += n4;
      n2 += n3;
   }

   if (n1 > 0 && n2 > 0)
      throw new Error("incompatible cis-trans configuration");

   if (n1 == 0 && n2 == 0)
      return false;

   if (n1 > 0)
   {
      this._dbonds[sidebonds[0]].saved =
         (mol.bonds.get(sidebonds[0]).begin == bond.begin) ? 1 : 2;
      if (sidebonds[1] != -1)
         this._dbonds[sidebonds[1]].saved =
            (mol.bonds.get(sidebonds[1]).begin == bond.begin) ? 2 : 1;

      this._dbonds[sidebonds[2]].saved =
         ((mol.bonds.get(sidebonds[2]).begin == bond.end) == (parity == chem.CisTrans.PARITY.CIS)) ? 1 : 2;
      if (sidebonds[3] != -1)
         this._dbonds[sidebonds[3]].saved =
            ((mol.bonds.get(sidebonds[3]).begin == bond.end) == (parity == chem.CisTrans.PARITY.CIS)) ? 2 : 1;
   }
   if (n2 > 0)
   {
      this._dbonds[sidebonds[0]].saved =
         (mol.bonds.get(sidebonds[0]).begin == bond.begin) ? 2 : 1;
      if (sidebonds[1] != -1)
         this._dbonds[sidebonds[1]].saved =
            (mol.bonds.get(sidebonds[1]).begin == bond.begin) ? 1 : 2;

      this._dbonds[sidebonds[2]].saved =
         ((mol.bonds.get(sidebonds[2]).begin == bond.end) == (parity == chem.CisTrans.PARITY.CIS)) ? 2 : 1;
      if (sidebonds[3] != -1)
         this._dbonds[sidebonds[3]].saved =
            ((mol.bonds.get(sidebonds[3]).begin == bond.end) == (parity == chem.CisTrans.PARITY.CIS)) ? 1 : 2;
   }

   return true;
};

chem.SmilesSaver.prototype._calcBondDirection = function (mol, idx, vprev)
{
   var ntouched;

   if (this._dbonds[idx].ctbond_beg == -1 && this._dbonds[idx].ctbond_end == -1)
      return 0;

   if (mol.bonds.get(idx).type != chem.Struct.BOND.TYPE.SINGLE)
      throw new Error("internal: directed bond type " + mol.bonds.get(idx).type);

   while (true)
   {
      ntouched = 0;
      this.cis_trans.each(function (bid, ct)
      {
         if (ct.parity != 0 && !this._render.isBondInRing(bid))
         {
            if (this._updateSideBonds(mol, bid))
               ntouched++;
         }
      }, this);
      if (ntouched == this._touched_cistransbonds)
         break;
      this._touched_cistransbonds = ntouched;
   }

   if (this._dbonds[idx].saved == 0)
   {
      if (vprev == mol.bonds.get(idx).begin)
         this._dbonds[idx].saved = 1;
      else
         this._dbonds[idx].saved = 2;
   }

   return this._dbonds[idx].saved;
};

chem.SmilesSaver.prototype._writeRadicals = function (mol)
{
   var marked = new Array(this._written_atoms.length);
   var i, j;

   for (i = 0; i < this._written_atoms.size(); i++)
   {
      if (marked[i])
         continue;

      var radical = mol.atoms.get(this._written_atoms[i]).radical;

      if (radical == 0)
         continue;

      if (this.comma)
         this.smiles += ',';
      else
      {
         this.smiles += ' |';
         this.comma = true;
      }

      if (radical == chem.Struct.ATOM.RADICAL.SINGLET)
         this.smiles += '^3:';
      else if (radical == chem.Struct.ATOM.RADICAL.DOUPLET)
         this.smiles += '^1:';
      else // RADICAL_TRIPLET
         this.smiles += '^4:';

      this.smiles += i;

      for (j = i + 1; j < this._written_atoms.length; j++)
         if (mol.atoms.get(this._written_atoms[j]).radical == radical)
         {
            marked[j] = true;
            this.smiles += ',' + j;
         }
   }
};

/*
void SmilesSaver::_writeStereogroups (const Struct &mol, const Array<_Atom> &atoms)
{
   MoleculeStereocenters &stereocenters = mol.getStereocenters();
   int i, j;
   int single_and_group = -1;

   for (i = stereocenters.begin(); i != stereocenters.end(); i = stereocenters.next(i))
   {
      int idx, type, group;

      stereocenters.get(i, idx, type, group, 0);

      if (type < MoleculeStereocenters::ATOM_ANY)
         continue;
      if (type != MoleculeStereocenters::ATOM_AND)
         break;
      if (single_and_group == -1)
         single_and_group = group;
      else if (single_and_group != group)
         break;
   }

   if (i == stereocenters.end())
      return;

   int and_group_idx = 1;
   int or_group_idx = 1;

   QS_DEF(Array<int>, marked);

   marked.clear_resize(_written_atoms.size());
   marked.zerofill();

   for (i = 0; i < _written_atoms.size(); i++)
   {
      if (marked[i])
         continue;

      int type = stereocenters.getType(_written_atoms[i]);

      if (type > 0)
      {
         if (_comma)
            _output.writeChar(',');
         else
         {
            _output.writeString(" |");
            _comma = true;
         }
      }

      if (type == MoleculeStereocenters::ATOM_ANY)
      {
         _output.printf("w:%d", i);

         for (j = i + 1; j < _written_atoms.size(); j++)
            if (stereocenters.getType(_written_atoms[j]) == MoleculeStereocenters::ATOM_ANY)
            {
               marked[j] = 1;
               _output.printf(",%d", j);
            }
      }
      else if (type == MoleculeStereocenters::ATOM_ABS)
      {
         _output.printf("a:%d", i);

         for (j = i + 1; j < _written_atoms.size(); j++)
            if (stereocenters.getType(_written_atoms[j]) == MoleculeStereocenters::ATOM_ABS)
            {
               marked[j] = 1;
               _output.printf(",%d", j);
            }
      }
      else if (type == MoleculeStereocenters::ATOM_AND)
      {
         int group = stereocenters.getGroup(_written_atoms[i]);

         _output.printf("&%d:%d", and_group_idx++, i);
         for (j = i + 1; j < _written_atoms.size(); j++)
            if (stereocenters.getType(_written_atoms[j]) == MoleculeStereocenters::ATOM_AND &&
                stereocenters.getGroup(_written_atoms[j]) == group)
            {
               marked[j] = 1;
               _output.printf(",%d", j);
            }
      }
      else if (type == MoleculeStereocenters::ATOM_OR)
      {
         int group = stereocenters.getGroup(_written_atoms[i]);

         _output.printf("o%d:%d", or_group_idx++, i);
         for (j = i + 1; j < _written_atoms.size(); j++)
            if (stereocenters.getType(_written_atoms[j]) == MoleculeStereocenters::ATOM_OR &&
                stereocenters.getGroup(_written_atoms[j]) == group)
            {
               marked[j] = 1;
               _output.printf(",%d", j);
            }
      }
   }
}
*/

/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.rnd)
	rnd = {};

rnd.MouseEvent = function (params)
{
    // TODO: deal with multitouch
    if ('touches' in params)
    {
        this.touches = params.touches.length;
        
        if (params.touches.length == 1)
        { // Only deal with one finger
            var touch = params.touches[0]; // Get the information for finger #1
            
            this.pageX = touch.pageX;
            this.pageY = touch.pageY;
        }
    } else
    {
        this.pageX = params.pageX;
        this.pageY = params.pageY;
    }
    
	if (Object.isUndefined(this.pageX) || Object.isUndefined(this.pageY))
	{ // TODO: fix this in IE
		this.pageX = params.x;
		this.pageY = params.y;
	}
	this.altKey = params.altKey;
	this.shiftKey = params.shiftKey;
	this.ctrlKey = params.ctrlKey;
	this.metaKey = params.metaKey;
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

// Visel is a shorthand for VISual ELement
// It corresponds to a visualization (i.e. set of paths) of an atom or a bond.
if (!window.chem || !util.Vec2 || !chem.Struct || !window.rnd)
	throw new Error("Vec2 and Molecule, should be defined first");

rnd.Visel = function (type)
{
	this.type = type;
	this.paths = [];
	this.boxes = [];
	this.boundingBox = null;
};

rnd.Visel.TYPE = {
    'ATOM' : 1,
    'BOND' : 2,
    'LOOP' : 3,
    'ARROW' : 4,
    'PLUS' : 5,
    'SGROUP' : 6,
    'TMP' : 7, // [MK] TODO: do we still need it?
    'FRAGMENT' : 8,
    'RGROUP' : 9,
    'CHIRAL_FLAG' : 10
};

rnd.Visel.prototype.add = function (path, bb)
{
	this.paths.push(path);
	if (bb != null) {
		this.boxes.push(bb);
		this.boundingBox = this.boundingBox == null ? bb : util.Box2Abs.union(this.boundingBox, bb);
	}
};

rnd.Visel.prototype.clear = function ()
{
	this.paths = [];
	this.boxes = [];
	this.boundingBox = null;
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOS  E.
 ***************************************************************************/

// rnd.ReStruct constructor and utilities are defined here
//
// ReStruct is to store all the auxiliary information for
//  chem.Struct while rendering
if (!window.chem || !util.Vec2 || !chem.Struct || !window.rnd || !rnd.Visel)
	throw new Error("Vec2, Molecule and Visel should be defined first");

if (!window.rnd)
	rnd = {};

rnd.ReObject = function()  // TODO ??? should it be in ReStruct namespace
{
    this.__ext = new util.Vec2(0.05 * 3, 0.05 * 3);
};

rnd.ReObject.prototype.init = function(viselType)
{
    this.visel = new rnd.Visel(viselType);

    this.highlight = false;
    this.highlighting = null;
    this.selected = false;
    this.selectionPlate = null;
};

rnd.ReObject.prototype.calcVBox = function(render) {
    if (this.visel.boundingBox) {
        return this.visel.boundingBox
            .transform(render.scaled2obj, render);
    }
};

rnd.ReObject.prototype.drawHighlight = function(render) {
    console.log('ReObject.drawHighlight is not overridden');
};

rnd.ReObject.prototype.setHighlight = function(highLight, render) { // TODO render should be field
    if (highLight) {
        var noredraw = this.highlighting && !this.highlighting.removed;
        // rbalabanov: here is temporary fix for "drag issue" on iPad
        //BEGIN
        noredraw = noredraw && (!('hiddenPaths' in rnd.ReStruct.prototype) || rnd.ReStruct.prototype.hiddenPaths.indexOf(this.highlighting) < 0);
        //END
        if (noredraw) {
            this.highlighting.show();
        }
        else this.highlighting = this.drawHighlight(render);
    } else {
        if (this.highlighting) this.highlighting.hide();
    }
    this.highlight = highLight;
};

rnd.ReObject.prototype.makeSelectionPlate = function(render) {
    console.log('ReObject.makeSelectionPlate is not overridden');
};

rnd.ReAtom = function (/*chem.Atom*/atom)
{
    this.init(rnd.Visel.TYPE.ATOM);

	this.a = atom; // TODO rename a to item
	this.showLabel = false;

	this.hydrogenOnTheLeft = false;

	this.sGroupHighlight = false;
	this.sGroupHighlighting = null;

	this.component = -1;
};
rnd.ReAtom.prototype = new rnd.ReObject();

rnd.ReAtom.prototype.drawHighlight = function(render) {
    var ps = render.ps(this.a.pp);
    var ret = render.paper.circle(
        ps.x, ps.y, render.styles.atomSelectionPlateRadius
    ).attr(render.styles.highlightStyle);
    render.addItemPath(this.visel, 'highlighting', ret);
    return ret;
};

rnd.ReAtom.prototype.makeSelectionPlate = function (restruct, paper, styles) {
    var ps = restruct.render.ps(this.a.pp);
	return paper.circle(ps.x, ps.y, styles.atomSelectionPlateRadius)
	.attr(styles.selectionStyle);
};

rnd.ReBond = function (/*chem.Bond*/bond)
{
    this.init(rnd.Visel.TYPE.BOND);

	this.b = bond; // TODO rename b to item
	this.doubleBondShift = 0;
};
rnd.ReBond.prototype = new rnd.ReObject();

rnd.ReBond.prototype.drawHighlight = function(render)
{
    render.ctab.bondRecalc(render.settings, this);
    var ret = render.paper.ellipse(
        this.b.center.x, this.b.center.y, this.b.sa, this.b.sb
    ).rotate(this.b.angle).attr(render.styles.highlightStyle);
    render.addItemPath(this.visel, 'highlighting', ret);
    return ret;
};

rnd.ReBond.prototype.makeSelectionPlate = function (restruct, paper, styles) {
	restruct.bondRecalc(restruct.render.settings, this);
	return paper
	.ellipse(this.b.center.x, this.b.center.y, this.b.sa, this.b.sb)
	.rotate(this.b.angle)
	.attr(styles.selectionStyle);
};

rnd.ReStruct = function (molecule, render, norescale)
{
	this.render = render;
	this.atoms = new util.Map();
	this.bonds = new util.Map();
	this.reloops = new util.Map();
	this.rxnPluses = new util.Map();
	this.rxnArrows = new util.Map();
    this.frags = new util.Map();
    this.rgroups = new util.Map();
    this.sgroups = new util.Map();
    this.sgroupData = new util.Map();
    this.chiralFlags = new util.Map();
	this.molecule = molecule || new chem.Struct();
	this.initialized = false;
	this.layers = [];
	this.initLayers();

	this.connectedComponents = new util.Pool();
	this.ccFragmentType = new util.Map();

	for (var map in rnd.ReStruct.maps) {
		this[map+'Changed'] = {};
	}
	this.structChanged = false;

	molecule.atoms.each(function(aid, atom){
		this.atoms.set(aid, new rnd.ReAtom(atom));
	}, this);

	molecule.bonds.each(function(bid, bond){
		this.bonds.set(bid, new rnd.ReBond(bond));
	}, this);

	molecule.loops.each(function(lid, loop){
		this.reloops.set(lid, new rnd.ReLoop(loop));
	}, this);

	molecule.rxnPluses.each(function(id, item){
		this.rxnPluses.set(id, new rnd.ReRxnPlus(item));
	}, this);

	molecule.rxnArrows.each(function(id, item){
		this.rxnArrows.set(id, new rnd.ReRxnArrow(item));
	}, this);

    molecule.frags.each(function(id, item) {
        this.frags.set(id, new rnd.ReFrag(item));
    }, this);

    molecule.rgroups.each(function(id, item) {
        this.rgroups.set(id, new rnd.ReRGroup(item));
    }, this);

    molecule.sgroups.each(function(id, item) {
        this.sgroups.set(id, new rnd.ReSGroup(item));
        if (item.type == 'DAT' && !item.data.attached) {
            this.sgroupData.set(id, new rnd.ReDataSGroupData(item)); // [MK] sort of a hack, we use the SGroup id for the data field id
        }
    }, this);
    
    if (molecule.isChiral) {
        var bb = molecule.getCoordBoundingBox();
        this.chiralFlags.set(0,new rnd.ReChiralFlag(new util.Vec2(bb.max.x, bb.min.y - 1)));
    }
        

	this.coordProcess(norescale);

	this.tmpVisels = [];
};

rnd.ReStruct.maps = {
    'atoms':       0,
    'bonds':       1,
    'rxnPluses':   2,
    'rxnArrows':   3,
    'frags':       4,
    'rgroups':     5,
    'sgroupData':  6,
    'chiralFlags': 7
};

rnd.ReStruct.prototype.connectedComponentRemoveAtom = function (aid, atom) {
	atom = atom || this.atoms.get(aid);
	if (atom.component < 0)
		return;
	var cc = this.connectedComponents.get(atom.component);
	util.Set.remove(cc, aid);
	if (util.Set.size(cc) < 1)
		this.connectedComponents.remove(atom.component);

	atom.component = -1;
};

rnd.ReStruct.prototype.printConnectedComponents = function () {
	var strs = [];
	this.connectedComponents.each(function(ccid, cc){
		strs.push(' ' + ccid + ':[' + util.Set.list(cc).toString() + '].' + util.Set.size(cc).toString());
	}, this);
	console.log(strs.toString());
};

rnd.ReStruct.prototype.clearConnectedComponents = function () {
	this.connectedComponents.clear();
	this.atoms.each(function(aid, atom) {
		atom.component = -1;
	});
};

rnd.ReStruct.prototype.getConnectedComponent = function (aid, adjacentComponents) {
	var list = (typeof(aid['length']) == 'number') ? util.array(aid) : [aid];
	var ids = util.Set.empty();

	while (list.length > 0) {
		(function() {
			var aid = list.pop();
			util.Set.add(ids, aid);
			var atom = this.atoms.get(aid);
			if (atom.component >= 0) {
				util.Set.add(adjacentComponents, atom.component);
			}
			for (var i = 0; i < atom.a.neighbors.length; ++i) {
				var neiId = this.molecule.halfBonds.get(atom.a.neighbors[i]).end;
				if (!util.Set.contains(ids, neiId))
					list.push(neiId);
			}
		}).apply(this);
	}

	return ids;
};

rnd.ReStruct.prototype.addConnectedComponent = function (ids) {
	var compId = this.connectedComponents.add(ids);
	var adjacentComponents = util.Set.empty();
	var atomIds = this.getConnectedComponent(util.Set.list(ids), adjacentComponents);
	util.Set.remove(adjacentComponents, compId);
	var type = -1;
	util.Set.each(atomIds, function(aid) {
		var atom = this.atoms.get(aid);
		atom.component = compId;
		if (atom.a.rxnFragmentType != -1) {
			if (type != -1 && atom.a.rxnFragmentType != type)
				throw new Error('reaction fragment type mismatch');
			type = atom.a.rxnFragmentType;
		}
	}, this);

	this.ccFragmentType.set(compId, type);
	return compId;
};

rnd.ReStruct.prototype.removeConnectedComponent = function (ccid) {
	util.Set.each(this.connectedComponents.get(ccid), function(aid) {
		this.atoms.get(aid).component = -1;
	}, this);
	return this.connectedComponents.remove(ccid);
};

rnd.ReStruct.prototype.connectedComponentMergeIn = function (ccid, set) {
	util.Set.each(set, function(aid) {
		this.atoms.get(aid).component = ccid;
	}, this);
	util.Set.mergeIn(this.connectedComponents.get(ccid), set);
};

rnd.ReStruct.prototype.assignConnectedComponents = function () {
	this.atoms.each(function(aid,atom){
		if (atom.component >= 0)
			return;
		var adjacentComponents = util.Set.empty();
		var ids = this.getConnectedComponent(aid, adjacentComponents);
		util.Set.each(adjacentComponents, function(ccid){
			this.removeConnectedComponent(ccid);
		}, this);
		this.addConnectedComponent(ids);
	}, this);
};

rnd.ReStruct.prototype.connectedComponentGetBoundingBox = function (ccid, cc, bb) {
	cc = cc || this.connectedComponents.get(ccid);
	bb = bb || {'min':null, 'max':null};
	util.Set.each(cc, function(aid) {
		var ps = this.render.ps(this.atoms.get(aid).a.pp);
		if (bb.min == null) {
			bb.min = bb.max = ps;
		} else {
			bb.min = bb.min.min(ps);
			bb.max = bb.max.max(ps);
		}
	}, this);
	return bb;
};

rnd.ReStruct.prototype.initLayers = function () {
	for (var group in rnd.ReStruct.layerMap)
		this.layers[rnd.ReStruct.layerMap[group]] =
		this.render.paper.rect(0, 0, 10, 10)
		.attr({
			'fill':'#000',
			'opacity':'0.0'
		}).toFront();
};

rnd.ReStruct.prototype.insertInLayer = function (lid, path) {
	path.insertBefore(this.layers[lid]);
};

rnd.ReStruct.prototype.clearMarks = function () {
	this.bondsChanged = {};
	this.atomsChanged = {};
	this.structChanged = false;
};

rnd.ReStruct.prototype.markItemRemoved = function () {
	this.structChanged = true;
};

rnd.ReStruct.prototype.markBond = function (bid, mark) {
	this.markItem('bonds', bid, mark);
};

rnd.ReStruct.prototype.markAtom = function (aid, mark) {
	this.markItem('atoms', aid, mark);
};

rnd.ReStruct.prototype.markItem = function (map, id, mark) {
	var mapChanged = this[map+'Changed'];
	mapChanged[id] = (typeof(mapChanged[id]) != 'undefined') ?
	Math.max(mark, mapChanged[id]) : mark;
	if (this[map].has(id))
		this.clearVisel(this[map].get(id).visel);
};

rnd.ReStruct.prototype.eachVisel = function (func, context) {

	for (var map in rnd.ReStruct.maps) {
		this[map].each(function(id, item){
			func.call(context, item.visel);
		}, this);
	}
	this.sgroups.each(function(sid, sgroup){
		func.call(context, sgroup.visel);
	}, this);
	this.reloops.each(function(rlid, reloop) {
		func.call(context, reloop.visel);
	}, this);
	for (var i = 0; i < this.tmpVisels.length; ++i)
		func.call(context, this.tmpVisels[i]);
};

rnd.ReStruct.prototype.translate = function (d) {
	this.eachVisel(function(visel){
		this.translateVisel(visel, d);
	}, this);
};

rnd.ReStruct.prototype.scale = function (s) {
	// NOTE: bounding boxes are not valid after scaling
	this.eachVisel(function(visel){
		this.scaleVisel(visel, s);
	}, this);
};

rnd.ReStruct.prototype.translateVisel = function (visel, d) {
	var i;
	for (i = 0; i < visel.paths.length; ++i)
		visel.paths[i].translate(d.x, d.y);
	for (i = 0; i < visel.boxes.length; ++i)
		visel.boxes[i].translate(d);
	if (visel.boundingBox != null)
		visel.boundingBox.translate(d);
};

rnd.ReStruct.prototype.scaleRPath = function (path, s) {
	if (path.type == "set") { // TODO: rework scaling
		for (var i = 0; i < path.length; ++i)
			this.scaleRPath(path[i], s);
	} else {
		if (!Object.isUndefined(path.attrs)) {
			if ('font-size' in path.attrs)
				path.attr('font-size', path.attrs['font-size'] * s);
			else if ('stroke-width' in path.attrs)
				path.attr('stroke-width', path.attrs['stroke-width'] * s);
		}
		path.scale(s, s, 0, 0);
	}
};

rnd.ReStruct.prototype.scaleVisel = function (visel, s) {
	for (var i = 0; i < visel.paths.length; ++i)
		this.scaleRPath(visel.paths[i], s);
};

rnd.ReStruct.prototype.clearVisels = function () {
	this.eachVisel(function(visel){
		this.clearVisel(visel);
	}, this);
};

rnd.ReStruct.prototype.update = function (force)
{
	force = force || !this.initialized;

	// check items to update
	var id;
	if (force) {
		(function(){
			for (var map in rnd.ReStruct.maps) {
				var mapChanged = this[map+'Changed'];
				this[map].each(function(id){
					mapChanged[id] = 1;
				}, this);
			}
		}).call(this);
	} else {
		// check if some of the items marked are already gone
		(function(){
			for (var map in rnd.ReStruct.maps) {
				var mapChanged = this[map+'Changed'];
				for (id in mapChanged)
					if (!this[map].has(id))
						delete mapChanged[id];
			}
		}).call(this);
	}
	for (id in this.atomsChanged)
		this.connectedComponentRemoveAtom(id);

    // clean up empty fragments
    // TODO: fragment removal should be triggered by the action responsible for the fragment contents removal and form an operation of its own
    var emptyFrags = this.frags.findAll(function(fid, frag) {
        return !frag.calcBBox(this.render, fid);
    }, this);
    for (var j = 0; j < emptyFrags.length; ++j) {
        var fid = emptyFrags[j];
        this.clearVisel(this.frags.get(fid).visel);
        this.frags.unset(fid);
        this.molecule.frags.remove(fid);
    }

	(function(){
		for (var map in rnd.ReStruct.maps) {
			var mapChanged = this[map+'Changed'];
			for (id in mapChanged) {
				this.clearVisel(this[map].get(id).visel);
				this.structChanged |= mapChanged[id] > 0;
			}
		}
	}).call(this);

	// TODO: when to update sgroup?
	this.sgroups.each(function(sid, sgroup){
            this.clearVisel(sgroup.visel);
            sgroup.highlighting = null;
            sgroup.selectionPlate = null;
	}, this);
	for (var i = 0; i < this.tmpVisels.length; ++i)
		this.clearVisel(this.tmpVisels[i]);
	this.tmpVisels.clear();

    // TODO [RB] need to implement update-on-demand for fragments and r-groups
    this.frags.each(function(frid, frag) {
        this.clearVisel(frag.visel);
    }, this);
    this.rgroups.each(function(rgid, rgroup) {
        this.clearVisel(rgroup.visel);
    }, this);

	if (force) { // clear and recreate all half-bonds
		this.clearConnectedComponents();
		this.molecule.initHalfBonds();
		this.molecule.initNeighbors();
	}

	// only update half-bonds adjacent to atoms that have moved
	this.updateHalfBonds();
	this.sortNeighbors();
	this.assignConnectedComponents();
//	this.printConnectedComponents();
	this.setImplicitHydrogen();
	this.setHydrogenPos();
	this.initialized = true;

	this.scaleCoordinates();
	var updLoops = force || this.structChanged;
	if (updLoops)
		this.updateLoops();
	this.setDoubleBondShift();
	this.checkLabelsToShow();
	this.showLabels();
	this.shiftBonds();
	this.showBonds();
	this.verifyLoops();
	if (updLoops)
		this.renderLoops();
	this.clearMarks();
	this.drawReactionSymbols();
	this.drawSGroups();
    this.drawFragments();
    this.drawRGroups();
        this.chiralFlags.each(function(id, item) {
            if (this.chiralFlagsChanged[id] > 0)
                item.draw(this.render);
        }, this);
	return true;
};

rnd.ReStruct.prototype.drawReactionSymbols = function ()
{
	var item;
    var id;
	for (id in this.rxnArrowsChanged) {
		item = this.rxnArrows.get(id);
		this.drawReactionArrow(id, item);
	}
	for (id in this.rxnPlusesChanged) {
		item = this.rxnPluses.get(id);
		this.drawReactionPlus(id, item);
	}
};

rnd.ReStruct.prototype.drawReactionArrow = function (id, item)
{
	var centre = this.render.ps(item.item.pp);
	var path = this.drawArrow(new util.Vec2(centre.x - this.render.scale, centre.y), new util.Vec2(centre.x + this.render.scale, centre.y));
	item.visel.add(path, util.Box2Abs.fromRelBox(rnd.relBox(path.getBBox())));
	var offset = this.render.offset;
	if (offset != null)
		path.translate(offset.x, offset.y);
};

rnd.ReStruct.prototype.drawReactionPlus = function (id, item)
{
	var centre = this.render.ps(item.item.pp);
	var path = this.drawPlus(centre);
	item.visel.add(path, util.Box2Abs.fromRelBox(rnd.relBox(path.getBBox())));
	var offset = this.render.offset;
	if (offset != null)
		path.translate(offset.x, offset.y);
};

rnd.ReStruct.prototype.drawSGroups = function ()
{
	this.sgroups.each(function (id, sgroup) {
		var path = sgroup.draw(this.render);
		this.addReObjectPath('data', sgroup.visel, path);
	}, this);
};

rnd.ReStruct.prototype.drawFragments = function() {
    this.frags.each(function(id, frag) {
        var path = frag.draw(this.render, id);
        if (path) this.addReObjectPath('data', frag.visel, path);
        // TODO fragment selection & highlighting
    }, this);
};

rnd.ReStruct.prototype.drawRGroups = function() {
    this.rgroups.each(function(id, rgroup) {
        var drawing = rgroup.draw(this.render);
        for (var group in drawing) {
            while (drawing[group].length > 0) {
                this.addReObjectPath(group, rgroup.visel, drawing[group].shift());
            }
        }
        // TODO rgroup selection & highlighting
    }, this);
};

rnd.ReStruct.prototype.eachCC = function (func, type, context) {
	this.connectedComponents.each(function(ccid, cc) {
		if (!type || this.ccFragmentType.get(ccid) == type)
			func.call(context || this, ccid, cc);
	}, this);
};

rnd.ReStruct.prototype.getGroupBB = function (type)
{
	var bb = {'min':null, 'max':null};

	this.eachCC(function(ccid, cc) {
		bb = this.connectedComponentGetBoundingBox(ccid, cc, bb);
	}, type, this);

	return bb;
};

rnd.ReStruct.prototype.updateHalfBonds = function () {
	for (var aid in this.atomsChanged) {
		if (this.atomsChanged[aid] < 1)
			continue;
		this.molecule.atomUpdateHalfBonds(aid);
	}
};

rnd.ReStruct.prototype.sortNeighbors = function () {
	// sort neighbor halfbonds in CCW order
	for (var aid in this.atomsChanged) {
		if (this.atomsChanged[aid] < 1)
			continue;
		this.molecule.atomSortNeighbors(aid);
	}
};

rnd.ReStruct.prototype.setHydrogenPos = function () {
	// check where should the hydrogen be put on the left of the label
	for (var aid in this.atomsChanged) {
		var atom = this.atoms.get(aid);

		if (atom.a.neighbors.length == 0) {
			var elem = chem.Element.getElementByLabel(atom.a.label);
			if (elem != null) {
				atom.hydrogenOnTheLeft = chem.Element.elements.get(elem).putHydrogenOnTheLeft;
			}
			continue;
		}
		var yl = 1, yr = 1, nl = 0, nr = 0;
		for (var i = 0; i < atom.a.neighbors.length; ++i) {
			var d = this.molecule.halfBonds.get(atom.a.neighbors[i]).dir;
			if (d.x <= 0) {
				yl = Math.min(yl, Math.abs(d.y));
				nl++;
			} else {
				yr = Math.min(yr, Math.abs(d.y));
				nr++;
			}
		}
		if (yl < 0.51 || yr < 0.51)
			atom.hydrogenOnTheLeft = yr < yl;
		else
			atom.hydrogenOnTheLeft = nr > nl;
	}
};

rnd.ReStruct.prototype.setImplicitHydrogen = function () {
	// calculate implicit hydrogens
	for (var aid in this.atomsChanged) {
		this.molecule.calcImplicitHydrogen(aid);
	}
};

rnd.ReLoop = function (loop)
{
	this.loop = loop;
	this.visel = new rnd.Visel(rnd.Visel.TYPE.LOOP);
	this.centre = new util.Vec2();
	this.radius = new util.Vec2();
};

rnd.ReStruct.prototype.findLoops = function ()
{
	var struct = this.molecule;
	// Starting from each half-bond not known to be in a loop yet,
	//  follow the 'next' links until the initial half-bond is reached or
	//  the length of the sequence exceeds the number of half-bonds available.
	// In a planar graph, as long as every bond is a part of some "loop" -
	//  either an outer or an inner one - every iteration either yields a loop
	//  or doesn't start at all. Thus this has linear complexity in the number
	//  of bonds for planar graphs.
	var j, k, c, loop, loopId;
	struct.halfBonds.each(function (i, hb) {
		if (hb.loop == -1)
		{
			for (j = i, c = 0, loop = [];
				c <= struct.halfBonds.count();
				j = struct.halfBonds.get(j).next, ++c)
				{
				if (c > 0 && j == i) {
					var totalAngle = 2 * Math.PI;
					var convex = true;
					for (k = 0; k < loop.length; ++k)
					{
						var hba = struct.halfBonds.get(loop[k]);
						var hbb = struct.halfBonds.get(loop[(k + 1) % loop.length]);
						var angle = Math.atan2(
								util.Vec2.cross(hba.dir, hbb.dir),
								util.Vec2.dot(hba.dir, hbb.dir));
						if (angle > 0)
							convex = false;
						if (hbb.contra == loop[k]) // back and force one edge
							totalAngle += Math.PI;
						else
							totalAngle += angle;
					}
					if (Math.abs(totalAngle) < Math.PI) // loop is internal
						loopId = struct.loops.add(new chem.Loop(loop, struct, convex));
					else
						loopId = -2;
					loop.each(function(hbid){
						struct.halfBonds.get(hbid).loop = loopId;
						this.markBond(struct.halfBonds.get(hbid).bid, 1);
					}, this);
					if (loopId >= 0)
						this.reloops.set(loopId, new rnd.ReLoop(struct.loops.get(loopId)));
					break;
				} else {
					loop.push(j);
				}
			}
		}
	}, this);
};

rnd.ReStruct.prototype.coordProcess = function (norescale)
{
    if (!norescale) {
        this.molecule.rescale();
    }
};

rnd.ReStruct.prototype.scaleCoordinates = function() // TODO: check if we need that and why
{
    var render = this.render;
	var settings = render.settings;
	var scale = function (item) {
		item.ps = render.ps(item.pp);
	};
        var id;
	for (id in this.atomsChanged) {
		scale(this.atoms.get(id).a);
	}
	for (id in this.rxnArrowsChanged) {
		scale(this.rxnArrows.get(id).item);
	}
	for (id in this.rxnPlusesChanged) {
		scale(this.rxnPluses.get(id).item);
	}
};

rnd.ReStruct.prototype.notifyAtomAdded = function(aid) {
    var atomData = new rnd.ReAtom(this.molecule.atoms.get(aid));
    atomData.component = this.connectedComponents.add(util.Set.single(aid));
    this.atoms.set(aid, atomData);
    this.markAtom(aid, 1);
};

rnd.ReStruct.prototype.notifyRxnPlusAdded = function(plid) {
    this.rxnPluses.set(plid, new rnd.ReRxnPlus(this.molecule.rxnPluses.get(plid)));
};

rnd.ReStruct.prototype.notifyRxnArrowAdded = function(arid) {
    this.rxnArrows.set(arid, new rnd.ReRxnArrow(this.molecule.rxnArrows.get(arid)));
};

rnd.ReStruct.prototype.notifyRxnArrowRemoved = function(arid) {
    this.markItemRemoved();
    this.clearVisel(this.rxnArrows.get(arid).visel);
    this.rxnArrows.unset(arid);
};

rnd.ReStruct.prototype.notifyRxnPlusRemoved = function(plid) {
    this.markItemRemoved();
    this.clearVisel(this.rxnPluses.get(plid).visel);
    this.rxnPluses.unset(plid);
};

rnd.ReStruct.prototype.notifyBondAdded = function(bid) {
    this.bonds.set(bid, new rnd.ReBond(this.molecule.bonds.get(bid)));
    this.markBond(bid, 1);
};

rnd.ReStruct.prototype.notifyAtomRemoved = function (aid) {
    var atom = this.atoms.get(aid);
    var set = this.connectedComponents.get(atom.component);
    util.Set.remove(set, aid);
    if (util.Set.size(set) == 0) {
        this.connectedComponents.remove(atom.component);
    }
	this.clearVisel(atom.visel);
	this.atoms.unset(aid);
    this.markItemRemoved();
};

rnd.ReStruct.prototype.notifyBondRemoved = function(bid) {
    var bond = this.bonds.get(bid);
    [bond.b.hb1, bond.b.hb2].each(function(hbid) {
        var hb = this.molecule.halfBonds.get(hbid);
        if (hb.loop >= 0)
            this.loopRemove(hb.loop);
    }, this);
    this.clearVisel(bond.visel);
    this.bonds.unset(bid);
    this.markItemRemoved();
};

rnd.ReStruct.prototype.loopRemove = function (loopId)
{
	if (!this.reloops.has(loopId))
		return;
	var reloop = this.reloops.get(loopId);
	this.clearVisel(reloop.visel);
	var bondlist = [];
	for (var i = 0; i < reloop.loop.hbs.length; ++i) {
		var hbid = reloop.loop.hbs[i];
		if (!this.molecule.halfBonds.has(hbid))
			continue;
		var hb = this.molecule.halfBonds.get(hbid);
		hb.loop = -1;
		this.markBond(hb.bid, 1);
		this.markAtom(hb.begin, 1);
		bondlist.push(hb.bid);
	}
	this.reloops.unset(loopId);
	this.molecule.loops.remove(loopId);
};

rnd.ReStruct.prototype.loopIsValid = function (rlid, reloop) {
	var loop = reloop.loop;
	var bad = false;
	loop.hbs.each(function(hbid){
		if (!this.molecule.halfBonds.has(hbid)) {
			bad = true;
		}
	}, this);
	return !bad;
};

rnd.ReStruct.prototype.verifyLoops = function ()
{
	var toRemove = [];
	this.reloops.each(function(rlid, reloop){
		if (!this.loopIsValid(rlid, reloop)) {
			toRemove.push(rlid);
		}
	}, this);
	for (var i = 0; i < toRemove.length; ++i) {
		this.loopRemove(toRemove[i]);
	}
};

rnd.ReStruct.prototype.BFS = function (onAtom, orig, context) {
	orig = orig-0;
	var queue = new Array();
	var mask = {};
	queue.push(orig);
	mask[orig] = 1;
	while (queue.length > 0) {
		var aid = queue.shift();
		onAtom.call(context, aid);
		var atom = this.atoms.get(aid);
		for (var i = 0; i < atom.a.neighbors.length; ++i) {
			var nei = atom.a.neighbors[i];
			var hb = this.molecule.halfBonds.get(nei);
			if (!mask[hb.end]) {
				mask[hb.end] = 1;
				queue.push(hb.end);
			}
		}
	}
};

rnd.ReRxnPlus = function (/*chem.RxnPlus*/plus)
{
    this.init(rnd.Visel.TYPE.PLUS);

	this.item = plus;
};
rnd.ReRxnPlus.prototype = new rnd.ReObject();

rnd.ReRxnPlus.findClosest = function (render, p) {
    var minDist;
    var ret;

    render.ctab.rxnPluses.each(function(id, plus) {
        var pos = plus.item.pp;
        var dist = Math.max(Math.abs(p.x - pos.x), Math.abs(p.y - pos.y));
        if (dist < 0.5 && (!ret || dist < minDist)) {
            minDist = dist;
            ret = {'id' : id, 'dist' : minDist};
        }
    });
    return ret;
}

rnd.ReRxnPlus.prototype.highlightPath = function(render) {
    var p = render.ps(this.item.pp);
    var s = render.settings.scaleFactor;
    return render.paper.rect(p.x - s/4, p.y - s/4, s/2, s/2, s/8);
}

rnd.ReRxnPlus.prototype.drawHighlight = function(render) {
    var ret = this.highlightPath(render).attr(render.styles.highlightStyle);
    render.addItemPath(this.visel, 'highlighting', ret);
    return ret;
};

rnd.ReRxnPlus.prototype.makeSelectionPlate = function (restruct, paper, styles) { // TODO [MK] review parameters
    return this.highlightPath(restruct.render).attr(styles.selectionStyle);
};

rnd.ReRxnArrow = function (/*chem.RxnArrow*/arrow)
{
    this.init(rnd.Visel.TYPE.ARROW);

    this.item = arrow;
};
rnd.ReRxnArrow.prototype = new rnd.ReObject();

rnd.ReRxnArrow.findClosest = function(render, p) {
    var minDist;
    var ret;

    render.ctab.rxnArrows.each(function(id, arrow) {
        var pos = arrow.item.pp;
        if (Math.abs(p.x - pos.x) < 1.0) {
            var dist = Math.abs(p.y - pos.y);
            if (dist < 0.3 && (!ret || dist < minDist)) {
                minDist = dist;
                ret = {'id' : id, 'dist' : minDist};
            }
        }
    });
    return ret;
};

rnd.ReRxnArrow.prototype.highlightPath = function(render) {
    var p = render.ps(this.item.pp);
    var s = render.settings.scaleFactor;
    return render.paper.rect(p.x - s, p.y - s/4, 2*s, s/2, s/8);
}

rnd.ReRxnArrow.prototype.drawHighlight = function(render) {
    var ret = this.highlightPath(render).attr(render.styles.highlightStyle);
    render.addItemPath(this.visel, 'highlighting', ret);
    return ret;
};

rnd.ReRxnArrow.prototype.makeSelectionPlate = function (restruct, paper, styles) {
    return this.highlightPath(restruct.render).attr(styles.selectionStyle);
};

rnd.ReFrag = function(/*chem.Struct.Fragment*/frag) {
    this.init(rnd.Visel.TYPE.FRAGMENT);

    this.item = frag;
};
rnd.ReFrag.prototype = new rnd.ReObject();

rnd.ReFrag.findClosest = function(render, p, skip, minDist) {
    minDist = Math.min(minDist || render.opt.selectionDistanceCoefficient, render.opt.selectionDistanceCoefficient);
    var ret;
    render.ctab.frags.each(function(fid, frag) {
        if (fid != skip) {
            var bb = frag.calcBBox(render, fid); // TODO any faster way to obtain bb?
            if (bb.p0.y < p.y && bb.p1.y > p.y && bb.p0.x < p.x && bb.p1.x > p.x) {
                var xDist = Math.min(Math.abs(bb.p0.x - p.x), Math.abs(bb.p1.x - p.x));
                if (!ret || xDist < minDist) {
                    minDist = xDist;
                    ret = { 'id' : fid, 'dist' : minDist };
                }
            }
        }
    });
    return ret;
};

rnd.ReFrag.prototype.fragGetAtoms = function(render, fid) {
    var ret = [];
    render.ctab.atoms.each(function(aid, atom) {
        if (atom.a.fragment == fid) {
            ret.push(aid);
        }
    }, this);
    return ret;
};

rnd.ReFrag.prototype.fragGetBonds = function(render, fid) {
    var ret = [];
    render.ctab.bonds.each(function(bid, bond) {
        if (render.ctab.atoms.get(bond.b.begin).a.fragment == fid &&
            render.ctab.atoms.get(bond.b.end).a.fragment == fid) {
            ret.push(bid);
        }
    }, this);
    return ret;
};

rnd.ReFrag.prototype.calcBBox = function(render, fid) { // TODO need to review parameter list
    var ret;
    render.ctab.atoms.each(function(aid, atom) {
        if (atom.a.fragment == fid) {
            // TODO ReObject.calcBBox to be used instead
            var bba = atom.visel.boundingBox;
            if (!bba) {
                bba = new util.Box2Abs(atom.a.pp, atom.a.pp);
                var ext = new util.Vec2(0.05 * 3, 0.05 * 3);
                bba = bba.extend(ext, ext);
            } else {
                bba = bba.transform(render.scaled2obj, render);
            }
            ret = (ret ? util.Box2Abs.union(ret, bba) : bba);
        }
    }, this);
    return ret;
};

rnd.ReFrag.prototype._draw = function(render, fid, attrs) { // TODO need to review parameter list
    var bb = this.calcBBox(render, fid);
    if (bb) {
        var p0 = render.obj2scaled(new util.Vec2(bb.p0.x, bb.p0.y));
        var p1 = render.obj2scaled(new util.Vec2(bb.p1.x, bb.p1.y));
        return render.paper.rect(p0.x, p0.y, p1.x - p0.x, p1.y - p0.y, 0).attr(attrs);
    } else {
        // TODO abnormal situation, empty fragments must be destroyed by tools
    }
};

rnd.ReFrag.prototype.draw = function(render, fid) { // TODO need to review parameter list
    return null;//this._draw(render, fid, { 'stroke' : 'lightgray' }); // [RB] for debugging only
};

rnd.ReFrag.prototype.drawHighlight = function(render) { // TODO need to review parameter list
    var fid = render.ctab.frags.keyOf(this);
    if (!Object.isUndefined(fid)) {
        var ret = this._draw(render, fid, render.styles.highlightStyle/*{ 'fill' : 'red' }*/);
        render.addItemPath(this.visel, 'highlighting', ret);
        return ret;
    } else {
        // TODO abnormal situation, fragment does not belong to the render
    }
};

rnd.ReRGroup = function(/*chem.Struct.RGroup*/rgroup) {
    this.init(rnd.Visel.TYPE.RGROUP);

    this.item = rgroup;
};
rnd.ReRGroup.prototype = new rnd.ReObject();

rnd.ReRGroup.findClosest = function(render, p, skip, minDist) {
    minDist = Math.min(minDist || render.opt.selectionDistanceCoefficient, render.opt.selectionDistanceCoefficient);
    var ret;
    render.ctab.rgroups.each(function(rgid, rgroup) {
        if (rgid != skip) {
            var bb = rgroup.calcVBox(render);
            if (bb.p0.y < p.y && bb.p1.y > p.y && bb.p0.x < p.x && bb.p1.x > p.x) {
                var xDist = Math.min(Math.abs(bb.p0.x - p.x), Math.abs(bb.p1.x - p.x));
                if (!ret || xDist < minDist) {
                    minDist = xDist;
                    ret = { 'id' : rgid, 'dist' : minDist };
                }
            }
        }
    });
    return ret;
};

rnd.ReRGroup.prototype.calcBBox = function(render) {
    var ret;
    this.item.frags.each(function(fnum, fid) {
        var bbf = render.ctab.frags.get(fid).calcBBox(render, fid);
        if (bbf) {
            ret = (ret ? util.Box2Abs.union(ret, bbf) : bbf);
        }
    });
    ret = ret.extend(this.__ext, this.__ext);
    return ret;
};

rnd.ReRGroup.prototype.draw = function(render) { // TODO need to review parameter list
    var bb = this.calcBBox(render);
    var settings = render.settings;
    if (bb) {
        var ret = { 'data' : [] };
        var p0 = render.obj2scaled(new util.Vec2(bb.p0.x, bb.p0.y));
        var p1 = render.obj2scaled(new util.Vec2(bb.p1.x, bb.p1.y));
        var brackets = render.paper.set();
        chem.SGroup.drawBrackets(brackets, render, render.paper, settings, render.styles, bb);
        ret.data.push(brackets);
        var key = render.ctab.rgroups.keyOf(this);
        var label = render.paper.text(p0.x, (p0.y + p1.y)/2, 'R' + key + '=')
            .attr({
				'font' : settings.font,
				'font-size' : settings.fontRLabel,
				'fill' : 'black'
			});
        var labelBox = rnd.relBox(label.getBBox());
        label.translate(-labelBox.width/2-settings.lineWidth, 0);
        var logicStyle = {
				'font' : settings.font,
				'font-size' : settings.fontRLogic,
				'fill' : 'black'
			};

        var logic = [];
        // TODO [RB] temporary solution, need to review
        //BEGIN
/*
        if (this.item.range.length > 0)
            logic.push(this.item.range);
        if (this.item.resth)
            logic.push("RestH");
        if (this.item.ifthen > 0)
            logic.push("IF R" + key.toString() + " THEN R" + this.item.ifthen.toString());
*/
        logic.push(
            (this.item.ifthen > 0 ? 'IF ' : '')
            + 'R' + key.toString()
            + (this.item.range.length > 0
                ? this.item.range.startsWith('>') || this.item.range.startsWith('<') || this.item.range.startsWith('=')
                    ? this.item.range
                    : '=' + this.item.range
                : '>0')
            + (this.item.resth ? ' (RestH)' : '')
            + (this.item.ifthen > 0 ? '\nTHEN R' + this.item.ifthen.toString() : '')
        );
        //END
        var shift = labelBox.height/2 + settings.lineWidth/2;
        for (var i = 0; i < logic.length; ++i) {
            var logicPath = render.paper.text(p0.x, (p0.y + p1.y)/2, logic[i]).attr(logicStyle);
            var logicBox = rnd.relBox(logicPath.getBBox());
            shift += logicBox.height/2;
            logicPath.translate(-logicBox.width/2-6*settings.lineWidth, shift);
            shift += logicBox.height/2 + settings.lineWidth/2;
            ret.data.push(logicPath);
        }
        ret.data.push(label);
        return ret;
    } else {
        // TODO abnormal situation, empty fragments must be destroyed by tools
        return {};
    }
};

rnd.ReRGroup.prototype._draw = function(render, rgid, attrs) { // TODO need to review parameter list
    var bb = this.calcVBox(render).extend(this.__ext, this.__ext);
    if (bb) {
        var p0 = render.obj2scaled(new util.Vec2(bb.p0.x, bb.p0.y));
        var p1 = render.obj2scaled(new util.Vec2(bb.p1.x, bb.p1.y));
        return render.paper.rect(p0.x, p0.y, p1.x - p0.x, p1.y - p0.y, 0).attr(attrs);
    }
};

rnd.ReRGroup.prototype.drawHighlight = function(render) { // TODO need to review parameter list
    var rgid = render.ctab.rgroups.keyOf(this);
    if (!Object.isUndefined(rgid)) {
        var ret = this._draw(render, rgid, render.styles.highlightStyle/*{ 'fill' : 'red' }*/);
        render.addItemPath(this.visel, 'highlighting', ret);
        return ret;
    } else {
        // TODO abnormal situation, fragment does not belong to the render
    }
};

rnd.ReSGroup = function(sgroup) {
    this.init(rnd.Visel.TYPE.SGROUP);

    this.item = sgroup;
};
rnd.ReSGroup.prototype = new rnd.ReObject();

rnd.ReSGroup.findClosest = function(render, p) {
	var ret = null;
	var minDist = render.opt.selectionDistanceCoefficient;
    render.ctab.molecule.sgroups.each(function(sgid, sg){
        var d = sg.bracketDir, n = d.rotateSC(1, 0);
        var pg = new util.Vec2(util.Vec2.dot(p, d), util.Vec2.dot(p, n));
        for (var i = 0; i < sg.areas.length; ++i) {
            var box = sg.areas[i];
            var inBox = box.p0.y < pg.y && box.p1.y > pg.y && box.p0.x < pg.x && box.p1.x > pg.x;
            var xDist = Math.min(Math.abs(box.p0.x - pg.x), Math.abs(box.p1.x - pg.x));
            if (inBox && (ret == null || xDist < minDist)) {
                ret = sgid;
                minDist = xDist;
            }
        }
	}, this);
	if (ret != null)
		return {
			'id':ret,
			'dist':minDist
		};
	return null;
};

rnd.ReSGroup.prototype.draw = function(render) { // TODO need to review parameter list
    return this.item.draw(render.ctab);
};

rnd.ReSGroup.prototype.drawHighlight = function(render) {
    var styles = render.styles;
    var settings = render.settings;
    var paper = render.paper;
    var sg = this.item;
    var bb = sg.bracketBox.transform(render.obj2scaled, render);
    var lw = settings.lineWidth;
    var vext = new util.Vec2(lw * 4, lw * 6);
    bb = bb.extend(vext, vext);
    var d = sg.bracketDir, n = d.rotateSC(1,0);
    var a0 = util.Vec2.lc2(d, bb.p0.x, n, bb.p0.y);
    var a1 = util.Vec2.lc2(d, bb.p0.x, n, bb.p1.y);
    var b0 = util.Vec2.lc2(d, bb.p1.x, n, bb.p0.y);
    var b1 = util.Vec2.lc2(d, bb.p1.x, n, bb.p1.y);

    var set = paper.set();
    sg.highlighting = paper
        .path("M{0},{1}L{2},{3}L{4},{5}L{6},{7}L{0},{1}", a0.x, a0.y, a1.x, a1.y, b1.x, b1.y, b0.x, b0.y)
        .attr(styles.highlightStyle);
    set.push(sg.highlighting);
    render.ctab.addReObjectPath('highlighting', this.visel, sg.highlighting);

    var atoms = chem.SGroup.getAtoms(render.ctab.molecule, sg);

    atoms.each(function (id)
    {
        var atom = render.ctab.atoms.get(id);
        var ps = render.ps(atom.a.pp);
        atom.sGroupHighlighting = paper
        .circle(ps.x, ps.y, 0.7 * styles.atomSelectionPlateRadius)
        .attr(styles.sGroupHighlightStyle);
        set.push(atom.sGroupHighlighting);
        render.ctab.addReObjectPath('highlighting', this.visel, atom.sGroupHighlighting);
    }, this);
    return set;
};

rnd.ReDataSGroupData = function (sgroup)
{
    this.init(rnd.Visel.TYPE.SGROUP_DATA);

	this.sgroup = sgroup;
};

rnd.ReDataSGroupData.prototype = new rnd.ReObject();

rnd.ReDataSGroupData.findClosest = function (render, p) {
    var minDist = null;
    var ret = null;

    render.ctab.sgroupData.each(function(id, item) {
        if (item.sgroup.type != 'DAT')
            throw new Error("Data group expected");
        var box = item.sgroup.dataArea;
        var inBox = box.p0.y < p.y && box.p1.y > p.y && box.p0.x < p.x && box.p1.x > p.x;
        var xDist = Math.min(Math.abs(box.p0.x - p.x), Math.abs(box.p1.x - p.x));
        if (inBox && (ret == null || xDist < minDist)) {
            ret = {'id' : id, 'dist' : xDist};
            minDist = xDist;
        }
    });
    return ret;
}

rnd.ReDataSGroupData.prototype.highlightPath = function(render) {
    var box = this.sgroup.dataArea;
    var p0 = render.obj2scaled(box.p0);
    var sz = render.obj2scaled(box.p1).sub(p0);
    return render.paper.rect(p0.x, p0.y, sz.x, sz.y);
}

rnd.ReDataSGroupData.prototype.drawHighlight = function(render) {
    var ret = this.highlightPath(render).attr(render.styles.highlightStyle);
    render.addItemPath(this.visel, 'highlighting', ret);
    return ret;
};

rnd.ReDataSGroupData.prototype.makeSelectionPlate = function (restruct, paper, styles) { // TODO [MK] review parameters
    return this.highlightPath(restruct.render).attr(styles.selectionStyle);
};

rnd.ReChiralFlag = function (pos)
{
    this.init(rnd.Visel.TYPE.CHIRAL_FLAG);
    
    this.pp = pos;
};
rnd.ReChiralFlag.prototype = new rnd.ReObject();

rnd.ReChiralFlag.findClosest = function(render, p) {
    var minDist;
    var ret;

    // there is only one chiral flag, but we treat it as a "map" for convenience
    render.ctab.chiralFlags.each(function(id, item) {
        var pos = item.pp;
        if (Math.abs(p.x - pos.x) < 1.0) {
            var dist = Math.abs(p.y - pos.y);
            if (dist < 0.3 && (!ret || dist < minDist)) {
                minDist = dist;
                ret = {'id' : id, 'dist' : minDist};
            }
        }
    });
    return ret;
};

rnd.ReChiralFlag.prototype.highlightPath = function(render) {
    var box = util.Box2Abs.fromRelBox(this.path.getBBox());
    var sz = box.p1.sub(box.p0);
    var p0 = box.p0.sub(render.offset);
    return render.paper.rect(p0.x, p0.y, sz.x, sz.y);
}

rnd.ReChiralFlag.prototype.drawHighlight = function(render) {
    var ret = this.highlightPath(render).attr(render.styles.highlightStyle);
    render.addItemPath(this.visel, 'highlighting', ret);
    return ret;
};

rnd.ReChiralFlag.prototype.makeSelectionPlate = function (restruct, paper, styles) {
    return this.highlightPath(restruct.render).attr(styles.selectionStyle);
};

rnd.ReChiralFlag.prototype.draw = function(render) {
    var paper = render.paper;
    var settings = render.settings;
    var ps = render.ps(this.pp);
    this.path = paper.text(ps.x, ps.y, "Chiral")
    .attr({
            'font' : settings.font,
            'font-size' : settings.fontsz,
            'fill' : '#000'
    });
    render.addItemPath(this.visel, 'data', this.path);
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.rnd || !rnd.ReStruct)
	throw new Error("MolData should be defined first");

rnd.relBox = function (box) {
    return {
        x: box.x, 
        y: box.y, 
        width: box.width, 
        height: box.height
    };
}

rnd.ReStruct.prototype.drawArrow = function (a, b)
{
	var width = 5, length = 7;
	var paper = this.render.paper;
	var styles = this.render.styles;
	return paper.path("M{0},{1}L{2},{3}L{4},{5}M{2},{3}L{4},{6}", a.x, a.y, b.x, b.y, b.x - length, b.y - width, b.y + width)
	.attr(styles.lineattr);
};

rnd.ReStruct.prototype.drawPlus = function (c)
{
	var s = this.render.scale/5;
	var paper = this.render.paper;
	var styles = this.render.styles;
	return paper.path("M{0},{4}L{0},{5}M{2},{1}L{3},{1}", c.x, c.y, c.x - s, c.x + s, c.y - s, c.y + s)
	.attr(styles.lineattr);
};

rnd.ReStruct.prototype.drawBondSingle = function (hb1, hb2)
{
	var a = hb1.p, b = hb2.p;
	var paper = this.render.paper;
	var styles = this.render.styles;
	return paper.path("M{0},{1}L{2},{3}", a.x, a.y, b.x, b.y)
	.attr(styles.lineattr);
};

rnd.ReStruct.prototype.drawBondSingleUp = function (hb1, hb2)
{
	var a = hb1.p, b = hb2.p, n = hb1.norm;
	var settings = this.render.settings;
	var paper = this.render.paper;
	var styles = this.render.styles;
	var bsp = settings.bondSpace;
	var b2 = b.addScaled(n, bsp);
	var b3 = b.addScaled(n, -bsp);
	return paper.path("M{0},{1}L{2},{3}L{4},{5},L{0},{1}",
		a.x, a.y, b2.x, b2.y, b3.x, b3.y)
	.attr(styles.lineattr).attr({
		'fill':'#000'
	});
};

rnd.ReStruct.prototype.drawBondSingleDown = function (hb1, hb2)
{
	var a = hb1.p, b = hb2.p, n = hb1.norm;
	var settings = this.render.settings;
	var paper = this.render.paper;
	var styles = this.render.styles;
	var bsp = settings.bondSpace;
	var d = b.sub(a);
	var len = d.length();
	d = d.normalized();
	var interval = 1.2 * settings.lineWidth;
	var nlines = Math.floor((len - settings.lineWidth) /
		(settings.lineWidth + interval)) + 1;
	var step = len / (nlines - 1);

	var path = "", p, q, r = a;
	for (var i = 0; i < nlines; ++i) {
		r = a.addScaled(d, step * i);
		p = r.addScaled(n, bsp * i / (nlines - 1));
		q = r.addScaled(n, -bsp * i / (nlines - 1));
		path += 'M' + p.x + ',' + p.y + 'L' + q.x + ',' + q.y;
	}
	return paper.path(path)
    .attr(styles.lineattr);
};

rnd.ReStruct.prototype.drawBondSingleEither = function (hb1, hb2)
{
	var a = hb1.p, b = hb2.p, n = hb1.norm;
	var settings = this.render.settings;
	var paper = this.render.paper;
	var styles = this.render.styles;
	var bsp = settings.bondSpace;
	var d = b.sub(a);
	var len = d.length();
	d = d.normalized();
	var interval = 0.6 * settings.lineWidth;
	var nlines = Math.floor((len - settings.lineWidth) /
		(settings.lineWidth + interval)) + 1;
	var step = len / (nlines - 1);

	var path = 'M' + a.x + ',' + a.y, r = a;
	for (var i = 0; i < nlines; ++i) {
		r = a.addScaled(d, step * i).addScaled(n,
			((i & 1) ? -1 : +1) * bsp * i / (nlines - 1));
		path += 'L' + r.x + ',' + r.y;
	}
	return paper.path(path)
	.attr(styles.lineattr);
};

rnd.ReStruct.prototype.getBondLineShift = function (cos, sin)
{
	if (sin < 0 || Math.abs(cos) > 0.9)
		return 0;
	return sin / (1 - cos);
};

rnd.ReStruct.prototype.drawBondDouble = function (hb1, hb2, bond, cis_trans)
{
	var a = hb1.p, b = hb2.p, n = hb1.norm, shift = cis_trans ? 0 : bond.doubleBondShift;
	var settings = this.render.settings;
	var paper = this.render.paper;
	var styles = this.render.styles;
	var bsp = settings.bondSpace / 2;
	var s1 = bsp, s2 = -bsp;
	s1 += shift * bsp;
	s2 += shift * bsp;
	var a2 = a.addScaled(n, s1);
	var b2 = b.addScaled(n, s1);
	var a3 = a.addScaled(n, s2);
	var b3 = b.addScaled(n, s2);

	var shiftA = !this.atoms.get(hb1.begin).showLabel;
	var shiftB = !this.atoms.get(hb2.begin).showLabel;
	if (shift > 0) {
		if (shiftA)
			a2 = a2.addScaled(hb1.dir, settings.bondSpace *
				this.getBondLineShift(hb1.rightCos, hb1.rightSin));
		if (shiftB)
			b2 = b2.addScaled(hb1.dir, -settings.bondSpace *
				this.getBondLineShift(hb2.leftCos, hb2.leftSin));
	} else if (shift < 0) {
		if (shiftA)
			a3 = a3.addScaled(hb1.dir, settings.bondSpace *
				this.getBondLineShift(hb1.leftCos, hb1.leftSin));
		if (shiftB)
			b3 = b3.addScaled(hb1.dir, -settings.bondSpace *
				this.getBondLineShift(hb2.rightCos, hb2.rightSin));
	}

	return paper.path(cis_trans ?
		"M{0},{1}L{6},{7}M{4},{5}L{2},{3}" :
		"M{0},{1}L{2},{3}M{4},{5}L{6},{7}",
		a2.x, a2.y, b2.x, b2.y, a3.x, a3.y, b3.x, b3.y)
	.attr(styles.lineattr);
};

rnd.ReStruct.makeStroke = function (a, b) {
	return 'M' + a.x.toString() + ',' + a.y.toString() +
		'L' + b.x.toString() + ',' + b.y.toString() + '	';
};

rnd.ReStruct.prototype.drawBondSingleOrDouble = function (hb1, hb2)
{
	var a = hb1.p, b = hb2.p, n = hb1.norm;
        var render = this.render;
	var settings = render.settings;
	var paper = render.paper;
	var styles = render.styles;
	var bsp = settings.bondSpace / 2;

	var nSect = (util.Vec2.dist(a, b) / (settings.bondSpace + settings.lineWidth)).toFixed()-0;
	if (!(nSect & 1))
		nSect += 1;
	var path = '', pp = a;

	for (var i = 1; i <= nSect; ++i) {
		var pi = util.Vec2.lc2(a, (nSect - i) / nSect, b, i / nSect);
		if (i & 1) {
			path += rnd.ReStruct.makeStroke(pp, pi);
		} else {
			path += rnd.ReStruct.makeStroke(pp.addScaled(n, bsp), pi.addScaled(n, bsp));
			path += rnd.ReStruct.makeStroke(pp.addScaled(n, -bsp), pi.addScaled(n, -bsp));
		}
		pp = pi;
	}

	return paper.path(path)
	.attr(styles.lineattr);
};

rnd.ReStruct.prototype.drawBondTriple = function (hb1, hb2)
{
	var a = hb1.p, b = hb2.p, n = hb1.norm;
        var render = this.render;
	var settings = render.settings;
	var paper = render.paper;
	var styles = render.styles;
	var a2 = a.addScaled(n, settings.bondSpace);
	var b2 = b.addScaled(n, settings.bondSpace);
	var a3 = a.addScaled(n, -settings.bondSpace);
	var b3 = b.addScaled(n, -settings.bondSpace);
	var shiftA = !this.atoms.get(hb1.begin).showLabel;
	var shiftB = !this.atoms.get(hb2.begin).showLabel;
	if (shiftA)
		a2 = a2.addScaled(hb1.dir, settings.bondSpace *
			this.getBondLineShift(hb1.rightCos, hb1.rightSin));
	if (shiftB)
		b2 = b2.addScaled(hb1.dir, -settings.bondSpace *
			this.getBondLineShift(hb2.leftCos, hb2.leftSin));
	if (shiftA)
		a3 = a3.addScaled(hb1.dir, settings.bondSpace *
			this.getBondLineShift(hb1.leftCos, hb1.leftSin));
	if (shiftB)
		b3 = b3.addScaled(hb1.dir, -settings.bondSpace *
			this.getBondLineShift(hb2.rightCos, hb2.rightSin));
	return paper.path("M{0},{1}L{2},{3}M{4},{5}L{6},{7}M{8},{9}L{10},{11}",
		a.x, a.y, b.x, b.y, a2.x, a2.y, b2.x, b2.y, a3.x, a3.y, b3.x, b3.y)
	.attr(styles.lineattr);
};

rnd.ReStruct.prototype.drawBondAromatic = function (hb1, hb2, bond, drawDashLine)
{
	if (!drawDashLine) {
		return this.drawBondSingle(hb1, hb2);
	}
	var shift = bond.doubleBondShift;
	var paper = this.render.paper;
	var paths = this.preparePathsForAromaticBond(hb1, hb2, shift);
	var l1 = paths[0], l2 = paths[1];
	(shift > 0 ? l1 : l2).attr({
		'stroke-dasharray':'- '
	});
	return paper.set([l1,l2]);
};

rnd.ReStruct.prototype.drawBondSingleOrAromatic = function (hb1, hb2, bond)
{
	var shift = bond.doubleBondShift;
	var paper = this.render.paper;
	var paths = this.preparePathsForAromaticBond(hb1, hb2, shift);
	var l1 = paths[0], l2 = paths[1];
	(shift > 0 ? l1 : l2).attr({
		'stroke-dasharray':'-.'
	});
	return paper.set([l1,l2]);
};

rnd.ReStruct.prototype.preparePathsForAromaticBond = function (hb1, hb2, shift)
{
	var settings = this.render.settings;
	var paper = this.render.paper;
	var styles = this.render.styles;
	var a = hb1.p, b = hb2.p, n = hb1.norm;
	var bsp = settings.bondSpace / 2;
	var s1 = bsp, s2 = -bsp;
	s1 += shift * bsp;
	s2 += shift * bsp;
	var a2 = a.addScaled(n, s1);
	var b2 = b.addScaled(n, s1);
	var a3 = a.addScaled(n, s2);
	var b3 = b.addScaled(n, s2);
	var shiftA = !this.atoms.get(hb1.begin).showLabel;
	var shiftB = !this.atoms.get(hb2.begin).showLabel;
	if (shift > 0) {
		if (shiftA)
			a2 = a2.addScaled(hb1.dir, settings.bondSpace *
				this.getBondLineShift(hb1.rightCos, hb1.rightSin));
		if (shiftB)
			b2 = b2.addScaled(hb1.dir, -settings.bondSpace *
				this.getBondLineShift(hb2.leftCos, hb2.leftSin));
	} else if (shift < 0) {
		if (shiftA)
			a3 = a3.addScaled(hb1.dir, settings.bondSpace *
				this.getBondLineShift(hb1.leftCos, hb1.leftSin));
		if (shiftB)
			b3 = b3.addScaled(hb1.dir, -settings.bondSpace *
				this.getBondLineShift(hb2.rightCos, hb2.rightSin));
	}
	var l1 = paper.path("M{0},{1}L{2},{3}",
		a2.x, a2.y, b2.x, b2.y).attr(styles.lineattr);
	var l2 = paper.path("M{0},{1}L{2},{3}",
		a3.x, a3.y, b3.x, b3.y).attr(styles.lineattr);
	return [l1, l2];
};


rnd.ReStruct.prototype.drawBondDoubleOrAromatic = function (hb1, hb2, bond)
{
	var shift = bond.doubleBondShift;
	var paper = this.render.paper;
	var paths = this.preparePathsForAromaticBond(hb1, hb2, shift);
	var l1 = paths[0], l2 = paths[1];
	l1.attr({'stroke-dasharray':'-.'});
	l2.attr({'stroke-dasharray':'-.'});
	return paper.set([l1,l2]);
};

rnd.ReStruct.prototype.drawBondAny = function (hb1, hb2)
{
	var a = hb1.p, b = hb2.p;
	var paper = this.render.paper;
	var styles = this.render.styles;
	return paper.path("M{0},{1}L{2},{3}", a.x, a.y, b.x, b.y)
	.attr(styles.lineattr).attr({
		'stroke-dasharray':'- '
	});
};

rnd.ReStruct.prototype.drawReactingCenter = function (bond, hb1, hb2)
{
	var a = hb1.p, b = hb2.p;
	var c = b.add(a).scaled(0.5);
	var d = b.sub(a).normalized();
	var n = d.rotateSC(1, 0);

	var paper = this.render.paper;
	var styles = this.render.styles;
	var settings = this.render.settings;

	var p = [];

	var lw = settings.lineWidth, bs = settings.bondSpace/2;
	var alongIntRc = lw, // half interval along for CENTER
		alongIntMadeBroken = 2 * lw, // half interval between along for MADE_OR_BROKEN
		alongSz = 1.5 * bs, // half size along for CENTER
		acrossInt = 1.5 * bs, // half interval across for CENTER
		acrossSz = 3.0 * bs, // half size across for all
		tiltTan = 0.2; // tangent of the tilt angle

	switch (bond.b.reactingCenterStatus)
	{
	case chem.Struct.BOND.REACTING_CENTER.NOT_CENTER: // X
		p.push(c.addScaled(n, acrossSz).addScaled(d, tiltTan * acrossSz));
		p.push(c.addScaled(n, -acrossSz).addScaled(d, -tiltTan * acrossSz));
		p.push(c.addScaled(n, acrossSz).addScaled(d, -tiltTan * acrossSz));
		p.push(c.addScaled(n, -acrossSz).addScaled(d, tiltTan * acrossSz));
		break;
	case chem.Struct.BOND.REACTING_CENTER.CENTER:  // #
		p.push(c.addScaled(n, acrossSz).addScaled(d, tiltTan * acrossSz).addScaled(d, alongIntRc));
		p.push(c.addScaled(n, -acrossSz).addScaled(d, -tiltTan * acrossSz).addScaled(d, alongIntRc));
		p.push(c.addScaled(n, acrossSz).addScaled(d, tiltTan * acrossSz).addScaled(d, -alongIntRc));
		p.push(c.addScaled(n, -acrossSz).addScaled(d, -tiltTan * acrossSz).addScaled(d, -alongIntRc));
		p.push(c.addScaled(d, alongSz).addScaled(n, acrossInt));
		p.push(c.addScaled(d, -alongSz).addScaled(n, acrossInt));
		p.push(c.addScaled(d, alongSz).addScaled(n, -acrossInt));
		p.push(c.addScaled(d, -alongSz).addScaled(n, -acrossInt));
		break;
//	case chem.Struct.BOND.REACTING_CENTER.UNCHANGED:  // o
//		//draw a circle
//		break;
	case chem.Struct.BOND.REACTING_CENTER.MADE_OR_BROKEN:
		p.push(c.addScaled(n, acrossSz).addScaled(d, alongIntMadeBroken));
		p.push(c.addScaled(n, -acrossSz).addScaled(d, alongIntMadeBroken));
		p.push(c.addScaled(n, acrossSz).addScaled(d, -alongIntMadeBroken));
		p.push(c.addScaled(n, -acrossSz).addScaled(d, -alongIntMadeBroken));
		break;
	case chem.Struct.BOND.REACTING_CENTER.ORDER_CHANGED:
		p.push(c.addScaled(n, acrossSz));
		p.push(c.addScaled(n, -acrossSz));
		break;
	case chem.Struct.BOND.REACTING_CENTER.MADE_OR_BROKEN_AND_CHANGED:
		p.push(c.addScaled(n, acrossSz).addScaled(d, alongIntMadeBroken));
		p.push(c.addScaled(n, -acrossSz).addScaled(d, alongIntMadeBroken));
		p.push(c.addScaled(n, acrossSz).addScaled(d, -alongIntMadeBroken));
		p.push(c.addScaled(n, -acrossSz).addScaled(d, -alongIntMadeBroken));
		p.push(c.addScaled(n, acrossSz));
		p.push(c.addScaled(n, -acrossSz));
		break;
	default:
		return null;
	}

	var pathdesc = "";
	for (var i = 0; i < p.length / 2; ++i)
		pathdesc += "M" + p[2 * i].x + "," + p[2 * i].y + "L" + p[2 * i + 1].x + "," + p[2 * i + 1].y;
	return paper.path(pathdesc).attr(styles.lineattr);
};

rnd.ReStruct.prototype.drawTopologyMark = function (bond, hb1, hb2)
{
	var topologyMark = null;

	if (bond.b.topology == chem.Struct.BOND.TOPOLOGY.RING)
		topologyMark = "rng";
	else if (bond.b.topology == chem.Struct.BOND.TOPOLOGY.CHAIN)
		topologyMark = "chn";
	else
		return null;

	var paper = this.render.paper;
	var settings = this.render.settings;

	var a = hb1.p, b = hb2.p;
	var c = b.add(a).scaled(0.5);
	var d = b.sub(a).normalized();
	var n = d.rotateSC(1, 0);
	var fixed = settings.lineWidth;
	if (bond.doubleBondShift > 0)
		n = n.scaled(-bond.doubleBondShift);
	else if (bond.doubleBondShift == 0)
		fixed += settings.bondSpace / 2;

	var s = new util.Vec2(2, 1).scaled(settings.bondSpace);
	if (bond.b.type == chem.Struct.BOND.TYPE.TRIPLE)
		fixed += settings.bondSpace;
	var p = c.add(new util.Vec2(n.x * (s.x + fixed), n.y * (s.y + fixed)));
	var path = paper.text(p.x, p.y, topologyMark)
		.attr({
			'font' : settings.font,
			'font-size' : settings.fontszsub,
			'fill' : '#000'
		});
	var rbb = rnd.relBox(path.getBBox());
	this.centerText(path, rbb);
	return path;
};

rnd.ReStruct.prototype.drawBond = function (bond, hb1, hb2)
{
	var path = null;
        var molecule = this.molecule;
	switch (bond.b.type)
	{
		case chem.Struct.BOND.TYPE.SINGLE:
			switch (bond.b.stereo) {
				case chem.Struct.BOND.STEREO.UP:
					path = this.drawBondSingleUp(hb1, hb2, bond);
					break;
				case chem.Struct.BOND.STEREO.DOWN:
					path = this.drawBondSingleDown(hb1, hb2, bond);
					break;
				case chem.Struct.BOND.STEREO.EITHER:
					path = this.drawBondSingleEither(hb1, hb2, bond);
					break;
				default:
					path = this.drawBondSingle(hb1, hb2);
					break;
			}
			break;
		case chem.Struct.BOND.TYPE.DOUBLE:
			path = this.drawBondDouble(hb1, hb2, bond,
				bond.b.stereo == chem.Struct.BOND.STEREO.CIS_TRANS);
			break;
		case chem.Struct.BOND.TYPE.TRIPLE:
			path = this.drawBondTriple(hb1, hb2, bond);
			break;
		case chem.Struct.BOND.TYPE.AROMATIC:
			var inAromaticLoop = (hb1.loop >= 0 && molecule.loops.get(hb1.loop).aromatic) ||
				(hb2.loop >= 0 && molecule.loops.get(hb2.loop).aromatic);
			path = this.drawBondAromatic(hb1, hb2, bond, !inAromaticLoop);
			break;
		case chem.Struct.BOND.TYPE.SINGLE_OR_DOUBLE:
			path = this.drawBondSingleOrDouble(hb1, hb2, bond);
			break;
		case chem.Struct.BOND.TYPE.SINGLE_OR_AROMATIC:
			path = this.drawBondSingleOrAromatic(hb1, hb2, bond);
			break;
		case chem.Struct.BOND.TYPE.DOUBLE_OR_AROMATIC:
			path = this.drawBondDoubleOrAromatic(hb1, hb2, bond);
			break;
		case chem.Struct.BOND.TYPE.ANY:
			path = this.drawBondAny(hb1, hb2, bond);
			break;
		default:
			throw new Error("Bond type " + bond.b.type + " not supported");
	}
	return path;
};

rnd.ReStruct.prototype.radicalCap = function (p)
{
	var settings = this.render.settings;
	var paper = this.render.paper;
	var s = settings.lineWidth * 0.9;
	var dw = s, dh = 2 * s;
	return paper.path("M{0},{1}L{2},{3}L{4},{5}",
		p.x - dw, p.y + dh, p.x, p.y, p.x + dw, p.y + dh)
	.attr({
		'stroke': '#000',
		'stroke-width': settings.lineWidth * 0.7,
		'stroke-linecap' : 'square',
		'stroke-linejoin' : 'miter'
	});
};

rnd.ReStruct.prototype.radicalBullet = function (p)
{
	var settings = this.render.settings;
	var paper = this.render.paper;
	return paper.circle(p.x, p.y, settings.lineWidth)
	.attr({
		stroke: null,
		fill: '#000'
	});
};

rnd.ReStruct.prototype.centerText = function (path, rbb)
{
	// TODO: find a better way
	if (this.render.paper.raphael.vml) {
		this.pathAndRBoxTranslate(path, rbb, 0, rbb.height * 0.16); // dirty hack
	}
};

// TODO to be removed
/** @deprecated please use ReAtom.setHighlight instead */
rnd.ReStruct.prototype.showAtomHighlighting = function (aid, atom, visible)
{
	var exists = (atom.highlighting != null) && !atom.highlighting.removed;
    // rbalabanov: here is temporary fix for "drag issue" on iPad
    //BEGIN
    exists = exists && (!('hiddenPaths' in rnd.ReStruct.prototype) || rnd.ReStruct.prototype.hiddenPaths.indexOf(atom.highlighting) < 0);
    //END
	if (visible) {
		if (!exists) {
			var render = this.render;
			var styles = render.styles;
			var paper = render.paper;
                        var ps = render.ps(atom.a.pp);
			atom.highlighting = paper
			.circle(ps.x, ps.y, styles.atomSelectionPlateRadius)
			.attr(styles.highlightStyle);
			if (rnd.DEBUG)
				atom.highlighting.attr({
					'fill':'#AAA'
				});
			render.addItemPath(atom.visel, 'highlighting', atom.highlighting);
		}
		if (rnd.DEBUG)
			atom.highlighting.attr({
				'stroke':'#0c0'
			});
		else
			atom.highlighting.show();
	} else {
		if (exists) {
			if (rnd.DEBUG)
				atom.highlighting.attr({
					'stroke':'none'
				});
			else
				atom.highlighting.hide();
		}
	}
};

rnd.ReStruct.prototype.showItemSelection = function (id, item, visible)
{
	var exists = (item.selectionPlate != null) && !item.selectionPlate.removed;
    // rbalabanov: here is temporary fix for "drag issue" on iPad
    //BEGIN
    exists = exists && (!('hiddenPaths' in rnd.ReStruct.prototype) || rnd.ReStruct.prototype.hiddenPaths.indexOf(item.selectionPlate) < 0);
    //END
	if (visible) {
		if (!exists) {
			var render = this.render;
			var styles = render.styles;
			var paper = render.paper;
			item.selectionPlate = item.makeSelectionPlate(this, paper, styles);
			render.addItemPath(item.visel, 'selection-plate', item.selectionPlate);
		}
		if (item.selectionPlate) item.selectionPlate.show(); // TODO [RB] review
	} else {
		if (exists)
			if (item.selectionPlate) item.selectionPlate.hide(); // TODO [RB] review
	}
};

rnd.ReStruct.prototype.pathAndRBoxTranslate = function (path, rbb, x, y)
{
	path.translate(x, y);
        rbb.x += x;
        rbb.y += y;
};

var markerColors = ['black', 'cyan', 'magenta', 'red', 'green', 'blue', 'green'];

rnd.ReStruct.prototype.showLabels = function ()
{
	var render = this.render;
	var settings = render.settings;
    var styles = render.styles;
	var opt = render.opt;
	var paper = render.paper;
	var delta = 0.5 * settings.lineWidth;
	for (var aid in this.atomsChanged) {
		var atom = this.atoms.get(aid);

                var ps = render.ps(atom.a.pp);
		var index = null;
		if (opt.showAtomIds) {
			index = {};
			index.text = aid.toString();
			index.path = paper.text(ps.x, ps.y, index.text)
			.attr({
				'font' : settings.font,
				'font-size' : settings.fontszsub,
				'fill' : '#070'
			});
			index.rbb = rnd.relBox(index.path.getBBox());
			this.centerText(index.path, index.rbb);
			render.addItemPath(atom.visel, 'indices', index.path, index.rbb);
		}
		if (atom.highlight)
			this.showAtomHighlighting(aid, atom, true);

        var color = '#000000';
		if (atom.showLabel)
		{
			var rightMargin = 0, leftMargin = 0;
			// label
			var label = {};
			if (atom.a.atomList != null) {
				label.text = atom.a.atomList.label();
            } else if (atom.a.label == 'R#' && atom.a.rglabel != null) {
                label.text = '';
                for (var rgi = 0; rgi < 32; rgi++) {
                    if (atom.a.rglabel & (1 << rgi)) label.text += ('R' + (rgi + 1).toString());
                }
                if (label.text == '') label = 'R#'; // for structures that missed 'M  RGP' tag in molfile
			} else {
				label.text = atom.a.label;
				if (opt.atomColoring) {
					var elem = chem.Element.getElementByLabel(label.text);
					if (elem)
						color = chem.Element.elements.get(elem).color;
				}
			}
			label.path = paper.text(ps.x, ps.y, label.text)
			.attr({
				'font' : settings.font,
				'font-size' : settings.fontsz,
				'fill' : color
			});
			label.rbb = rnd.relBox(label.path.getBBox());
			this.centerText(label.path, label.rbb);
			if (atom.a.atomList != null)
				this.pathAndRBoxTranslate(label.path, label.rbb, (atom.hydrogenOnTheLeft ? -1 : 1) * (label.rbb.width - label.rbb.height) / 2, 0);
			render.addItemPath(atom.visel, 'data', label.path, label.rbb);
			rightMargin = label.rbb.width / 2;
			leftMargin = -label.rbb.width / 2;
			var implh = Math.floor(atom.a.implicitH);
			var isHydrogen = label.text == 'H';
			var hydrogen = {}, hydroIndex = null;
			var hydrogenLeft = atom.hydrogenOnTheLeft;
			if (isHydrogen && implh > 0) {
				hydroIndex = {};
				hydroIndex.text = (implh+1).toString();
				hydroIndex.path =
				paper.text(ps.x, ps.y, hydroIndex.text)
				.attr({
					'font' : settings.font,
					'font-size' : settings.fontszsub,
					'fill' : color
				});
				hydroIndex.rbb = rnd.relBox(hydroIndex.path.getBBox());
				this.centerText(hydroIndex.path, hydroIndex.rbb);
				this.pathAndRBoxTranslate(hydroIndex.path, hydroIndex.rbb,
					rightMargin + 0.5 * hydroIndex.rbb.width + delta,
					0.2 * label.rbb.height);
				rightMargin += hydroIndex.rbb.width + delta;
				render.addItemPath(atom.visel, 'data', hydroIndex.path, hydroIndex.rbb);
			}

			var radical = {};
			if (atom.a.radical != 0)
			{
				var hshift;
				switch (atom.a.radical)
				{
					case 1:
						radical.path = paper.set();
						hshift = 1.6 * settings.lineWidth;
						radical.path.push(
							this.radicalBullet(ps).translate(-hshift, 0),
							this.radicalBullet(ps).translate(hshift, 0));
						radical.path.attr('fill', color);
						break;
					case 2:
						radical.path = this.radicalBullet(ps)
						.attr('fill', color);
						break;
					case 3:
						radical.path = paper.set();
						hshift = 1.6 * settings.lineWidth;
						radical.path.push(
							this.radicalCap(ps).translate(-hshift, 0),
							this.radicalCap(ps).translate(hshift, 0));
						radical.path.attr('stroke', color);
						break;
				}
				radical.rbb = rnd.relBox(radical.path.getBBox());
				var vshift = -0.5 * (label.rbb.height + radical.rbb.height);
				if (atom.a.radical == 3)
					vshift -= settings.lineWidth/2;
				this.pathAndRBoxTranslate(radical.path, radical.rbb,
					0, vshift);
				render.addItemPath(atom.visel, 'data', radical.path, radical.rbb);
			}

			var isotope = {};
			if (atom.a.isotope != 0)
			{
				isotope.text = atom.a.isotope.toString();
				isotope.path = paper.text(ps.x, ps.y, isotope.text)
				.attr({
					'font' : settings.font,
					'font-size' : settings.fontszsub,
					'fill' : color
				});
				isotope.rbb = rnd.relBox(isotope.path.getBBox());
				this.centerText(isotope.path, isotope.rbb);
				this.pathAndRBoxTranslate(isotope.path, isotope.rbb,
					leftMargin - 0.5 * isotope.rbb.width - delta,
					-0.3 * label.rbb.height);
				leftMargin -= isotope.rbb.width + delta;
				render.addItemPath(atom.visel, 'data', isotope.path, isotope.rbb);
			}
			if (!isHydrogen && implh > 0 && !render.opt.hideImplicitHydrogen)
			{
				hydrogen.text = 'H';
				hydrogen.path = paper.text(ps.x, ps.y, hydrogen.text)
				.attr({
					'font' : settings.font,
					'font-size' : settings.fontsz,
					'fill' : color
				});
				hydrogen.rbb = rnd.relBox(hydrogen.path.getBBox());
				this.centerText(hydrogen.path, hydrogen.rbb);
				if (!hydrogenLeft) {
					this.pathAndRBoxTranslate(hydrogen.path, hydrogen.rbb,
						rightMargin + 0.5 * hydrogen.rbb.width + delta, 0);
					rightMargin += hydrogen.rbb.width + delta;
				}
				if (implh > 1) {
					hydroIndex = {};
					hydroIndex.text = implh.toString();
					hydroIndex.path =
					paper.text(ps.x, ps.y, hydroIndex.text)
					.attr({
						'font' : settings.font,
						'font-size' : settings.fontszsub,
						'fill' : color
					});
					hydroIndex.rbb = rnd.relBox(hydroIndex.path.getBBox());
					this.centerText(hydroIndex.path, hydroIndex.rbb);
					if (!hydrogenLeft) {
						this.pathAndRBoxTranslate(hydroIndex.path, hydroIndex.rbb,
							rightMargin + 0.5 * hydroIndex.rbb.width + delta,
							0.2 * label.rbb.height);
						rightMargin += hydroIndex.rbb.width + delta;
					}
				}
				if (hydrogenLeft) {
					if (hydroIndex != null) {
						this.pathAndRBoxTranslate(hydroIndex.path, hydroIndex.rbb,
							leftMargin - 0.5 * hydroIndex.rbb.width - delta,
							0.2 * label.rbb.height);
						leftMargin -= hydroIndex.rbb.width + delta;
					}
					this.pathAndRBoxTranslate(hydrogen.path, hydrogen.rbb,
						leftMargin - 0.5 * hydrogen.rbb.width - delta, 0);
					leftMargin -= hydrogen.rbb.width + delta;
				}
				render.addItemPath(atom.visel, 'data', hydrogen.path, hydrogen.rbb);
				if (hydroIndex != null)
					render.addItemPath(atom.visel, 'data', hydroIndex.path, hydroIndex.rbb);
			}

			var charge = {};
			if (atom.a.charge != 0)
			{
				charge.text = "";
				var absCharge = Math.abs(atom.a.charge);
				if (absCharge != 1)
					charge.text = absCharge.toString();
				if (atom.a.charge < 0)
					charge.text += "\u2013";
				else
					charge.text += "+";

				charge.path = paper.text(ps.x, ps.y, charge.text)
				.attr({
					'font' : settings.font,
					'font-size' : settings.fontszsub,
					'fill' : color
				});
				charge.rbb = rnd.relBox(charge.path.getBBox());
				this.centerText(charge.path, charge.rbb);
				this.pathAndRBoxTranslate(charge.path, charge.rbb,
					rightMargin + 0.5 * charge.rbb.width + delta,
					-0.3 * label.rbb.height);
				rightMargin += charge.rbb.width + delta;
				render.addItemPath(atom.visel, 'data', charge.path, charge.rbb);
			}

			var valence = {};
			var mapValence = {
				0: '0',
				1: 'I',
				2: 'II',
				3: 'III',
				4: 'IV',
				5: 'V',
				6: 'VI',
				7: 'VII',
				8: 'VIII',
				9: 'IX',
				10: 'X',
				11: 'XI',
				12: 'XII',
				13: 'XIII',
				14: 'XIV'
			};
			if (atom.a.explicitValence)
			{
				valence.text = mapValence[atom.a.valence];
				if (!valence.text)
					throw new Error("invalid valence " + atom.a.valence.toString());
				valence.text = '(' + valence.text + ')';
				valence.path = paper.text(ps.x, ps.y, valence.text)
				.attr({
					'font' : settings.font,
					'font-size' : settings.fontszsub,
					'fill' : color
				});
				valence.rbb = rnd.relBox(valence.path.getBBox());
				this.centerText(valence.path, valence.rbb);
				this.pathAndRBoxTranslate(valence.path, valence.rbb,
					rightMargin + 0.5 * valence.rbb.width + delta,
					-0.3 * label.rbb.height);
				rightMargin += valence.rbb.width + delta;
				render.addItemPath(atom.visel, 'data', valence.path, valence.rbb);
			}

			if (atom.a.badConn && opt.showValenceWarnings) {
				var warning = {};
				var y = ps.y + label.rbb.height / 2 + delta;
				warning.path = paper.path("M{0},{1}L{2},{3}",
					ps.x + leftMargin, y, ps.x + rightMargin, y)
				.attr(this.render.styles.lineattr)
				.attr({
					'stroke':'#F00'
				});
				warning.rbb = rnd.relBox(warning.path.getBBox());
				render.addItemPath(atom.visel, 'warnings', warning.path, warning.rbb);
			}
			if (index)
				this.pathAndRBoxTranslate(index.path, index.rbb,
					-0.5 * label.rbb.width - 0.5 * index.rbb.width - delta,
					0.3 * label.rbb.height);
		}

        var lsb = this.bisectLargestSector(atom);

        var asterisk = Prototype.Browser.IE ? '*' : '∗';
        if (atom.a.attpnt) {
            var i, j;
            for (i = 0, c = 0; i < 4; ++i) {
                var attpntText = "";
                if (atom.a.attpnt & (1 << i)) {
                    if (attpntText.length > 0)
                        attpntText += ' ';
                    attpntText += asterisk;
                    for (j = 0; j < (i == 0 ? 0 : (i + 1)); ++j) {
                        attpntText += "'";
                    }
                    var pos0 = new util.Vec2(ps);
                    var pos1 = ps.addScaled(lsb, 0.7 * settings.scaleFactor);

                    var attpntPath1 = paper.text(pos1.x, pos1.y, attpntText)
                        .attr({
                            'font' : settings.font,
                            'font-size' : settings.fontsz,
                            'fill' : color
                        });
                    var attpntRbb = rnd.relBox(attpntPath1.getBBox());
                    this.centerText(attpntPath1, attpntRbb);

                    var lsbn = lsb.negated();
                    pos1 = pos1.addScaled(lsbn, util.Vec2.shiftRayBox(pos1, lsbn, util.Box2Abs.fromRelBox(attpntRbb)) + settings.lineWidth/2);
                    pos0 = this.shiftBondEnd(atom, pos0, lsb, settings.lineWidth);
                    var n = lsb.rotateSC(1, 0);
                    var arrowLeft = pos1.addScaled(n, 0.05 * settings.scaleFactor).addScaled(lsbn, 0.09 * settings.scaleFactor);
                    var arrowRight = pos1.addScaled(n, -0.05 * settings.scaleFactor).addScaled(lsbn, 0.09 * settings.scaleFactor);
                    var attpntPath = paper.set();
                    attpntPath.push(
                        attpntPath1,
                        paper.path("M{0},{1}L{2},{3}M{4},{5}L{2},{3}L{6},{7}", pos0.x, pos0.y, pos1.x, pos1.y, arrowLeft.x, arrowLeft.y, arrowRight.x, arrowRight.y)
                            .attr(styles.lineattr).attr({'stroke-width': settings.lineWidth/2})
                    );
                    render.addItemPath(atom.visel, 'indices', attpntPath, attpntRbb);
                    lsb = lsb.rotate(Math.PI/6);
                }
            }
        }


		var aamText = "";
		if (atom.a.aam > 0) {
			aamText += atom.a.aam;
		}
		if (atom.a.invRet > 0) {
			if (aamText.length > 0)
				aamText += ",";
			if (atom.a.invRet == 1)
				aamText += 'Inv';
			else if (atom.a.invRet == 2)
				aamText += 'Ret';
			else
				throw new Error('Invalid value for the invert/retain flag');
		}

        var queryAttrsText = "";
        if (atom.a.ringBondCount != 0) {
            if (atom.a.ringBondCount > 0)
                queryAttrsText += "rb" + atom.a.ringBondCount.toString();
            else if (atom.a.ringBondCount == -1)
                queryAttrsText += "rb0";
            else if (atom.a.ringBondCount == -2)
                queryAttrsText += "rb*";
            else
                throw new Error("Ring bond count invalid");
        }
        if (atom.a.substitutionCount != 0) {
            if (queryAttrsText.length > 0)
                queryAttrsText += ",";

            if (atom.a.substitutionCount > 0)
                queryAttrsText += "s" + atom.a.substitutionCount.toString();
            else if (atom.a.substitutionCount == -1)
                queryAttrsText += "s0";
            else if (atom.a.substitutionCount == -2)
                queryAttrsText += "s*";
            else
                throw new Error("Substitution count invalid");
        }
        if (atom.a.unsaturatedAtom > 0) {
            if (queryAttrsText.length > 0)
                queryAttrsText += ",";

            if (atom.a.unsaturatedAtom == 1)
                queryAttrsText += "u";
            else
                throw new Error("Unsaturated atom invalid value");
        }
        if (atom.a.hCount > 0) {
            if (queryAttrsText.length > 0)
                queryAttrsText += ",";

            queryAttrsText += "H" + (atom.a.hCount - 1).toString();
        }

        if (queryAttrsText.length > 0) {
            var queryAttrsPath = paper.text(ps.x, ps.y, queryAttrsText)
                .attr({
                    'font' : settings.font,
                    'font-size' : settings.fontszsub,
                    'fill' : color
                });
            var queryAttrsRbb = rnd.relBox(queryAttrsPath.getBBox());
            this.centerText(queryAttrsPath, queryAttrsRbb);
            this.pathAndRBoxTranslate(queryAttrsPath, queryAttrsRbb, 0, settings.scaleFactor / 3);
            render.addItemPath(atom.visel, 'indices', queryAttrsPath, queryAttrsRbb);
        }

		if (atom.a.exactChangeFlag > 0) {
			if (aamText.length > 0)
				aamText += ",";
			if (atom.a.exactChangeFlag == 1)
				aamText += 'ext';
			else
				throw new Error('Invalid value for the exact change flag');
		}
        if (aamText.length > 0) {
            var aamPath = paper.text(ps.x, ps.y, '.' + aamText + '.')
                .attr({
                    'font' : settings.font,
                    'font-size' : settings.fontszsub,
                    'fill' : color
                });
			var aamBox = rnd.relBox(aamPath.getBBox());
			this.centerText(aamPath, aamBox);

            var dir = this.bisectLargestSector(atom);

			var visel = atom.visel;
			var t = 3;
			for (i = 0; i < visel.boxes.length; ++i)
				t = Math.max(t, util.Vec2.shiftRayBox(ps, dir, visel.boxes[i]));
			dir = dir.scaled(10 + t);
			this.pathAndRBoxTranslate(aamPath, aamBox, dir.x, dir.y);
			render.addItemPath(atom.visel, 'data', aamPath, null);
        }
	}
};

rnd.ReStruct.prototype.shiftBondEnd = function (atom, pos0, dir, margin){
    var t = 0;
    var visel = atom.visel;
    for (var k = 0; k < visel.boxes.length; ++k)
        t = Math.max(t, util.Vec2.shiftRayBox(pos0, dir, visel.boxes[k]));
    if (t > 0)
        pos0 = pos0.addScaled(dir, t + margin);
    return pos0;
};

rnd.ReStruct.prototype.bisectLargestSector = function (atom)
{
    var angles = [];
    atom.a.neighbors.each( function (hbid) {
        var hb = this.molecule.halfBonds.get(hbid);
        angles.push(hb.ang);
    }, this);
    angles = angles.sort(function(a,b){return a-b;});
    var da = [];
    for (var i = 0; i < angles.length - 1; ++i) {
        da.push(angles[(i + 1) % angles.length] - angles[i]);
    }
    da.push(angles[0] - angles[angles.length - 1] + 2 * Math.PI);
    var daMax = 0;
    var ang = -Math.PI/2;
    for (i = 0; i < angles.length; ++i) {
        if (da[i] > daMax) {
            daMax = da[i];
            ang = angles[i] + da[i]/2;
        }
    }
    return new util.Vec2(Math.cos(ang), Math.sin(ang));
};


// TODO to be removed
/** @deprecated please use ReBond.setHighlight instead */
rnd.ReStruct.prototype.showBondHighlighting = function (bid, bond, visible)
{
	var exists = (bond.highlighting != null) && !bond.highlighting.removed;
    // rbalabanov: here is temporary fix for "drag issue" on iPad
    //BEGIN
    exists = exists && (!('hiddenPaths' in rnd.ReStruct.prototype) || rnd.ReStruct.prototype.hiddenPaths.indexOf(bond.highlighting) < 0);
    //END
	if (visible) {
		if (!exists) {
			var render = this.render;
			var styles = render.styles;
			var paper = render.paper;
			this.bondRecalc(render.settings, bond);
			bond.highlighting = paper
			.ellipse(bond.b.center.x, bond.b.center.y, bond.b.sa, bond.b.sb)
			.rotate(bond.b.angle)
			.attr(styles.highlightStyle);
			if (rnd.DEBUG)
				bond.highlighting.attr({
					'fill':'#AAA'
				});
			render.addItemPath(bond.visel, 'highlighting', bond.highlighting);
		}
		if (rnd.DEBUG)
			bond.highlighting.attr({
				'stroke':'#0c0'
			});
		else
			bond.highlighting.show();
	} else {
		if (exists) {
			if (rnd.DEBUG)
				bond.highlighting.attr({
					'stroke':'none'
				});
			else
				bond.highlighting.hide();
		}
	}
};

rnd.ReStruct.prototype.bondRecalc = function (settings, bond) {

    var render = this.render;
        var p1 = render.ps(this.atoms.get(bond.b.begin).a.pp);
        var p2 = render.ps(this.atoms.get(bond.b.end).a.pp);
	var hb1 = this.molecule.halfBonds.get(bond.b.hb1);
	bond.b.center = util.Vec2.lc2(p1, 0.5, p2, 0.5);
	bond.b.len = util.Vec2.dist(p1, p2);
	bond.b.sb = settings.lineWidth * 5;
	bond.b.sa = Math.max(bond.b.sb,  bond.b.len / 2 - settings.lineWidth * 2);
	bond.b.angle = Math.atan2(hb1.dir.y, hb1.dir.x) * 180 / Math.PI;
};

rnd.ReStruct.prototype.showBonds = function ()
{
	var render = this.render;
	var settings = render.settings;
	var paper = render.paper;
	var opt = render.opt;
	for (var bid in this.bondsChanged) {
		var bond = this.bonds.get(bid);
		var hb1 = this.molecule.halfBonds.get(bond.b.hb1),
		hb2 = this.molecule.halfBonds.get(bond.b.hb2);
		this.bondRecalc(settings, bond);
		bond.path = this.drawBond(bond, hb1, hb2);
		bond.rbb = rnd.relBox(bond.path.getBBox());
		render.addItemPath(bond.visel, 'data', bond.path, bond.rbb);
		var reactingCenter = {};
		reactingCenter.path = this.drawReactingCenter(bond, hb1, hb2);
		if (reactingCenter.path) {
			reactingCenter.rbb = rnd.relBox(reactingCenter.path.getBBox());
			render.addItemPath(bond.visel, 'data', reactingCenter.path, reactingCenter.rbb);
		}
		var topology = {};
		topology.path = this.drawTopologyMark(bond, hb1, hb2);
		if (topology.path) {
			topology.rbb = rnd.relBox(topology.path.getBBox());
			render.addItemPath(bond.visel, 'data', topology.path, topology.rbb);
		}
		if (bond.highlight)
			this.showBondHighlighting(bid, bond, true);
		var bondIdxOff = settings.subFontSize * 0.6;
		var ipath = null, irbb = null;
		if (opt.showBondIds) {
			var pb = util.Vec2.lc(hb1.p, 0.5, hb2.p, 0.5, hb1.norm, bondIdxOff);
			ipath = paper.text(pb.x, pb.y, bid.toString());
			irbb = rnd.relBox(ipath.getBBox());
			this.centerText(ipath, irbb);
			render.addItemPath(bond.visel, 'indices', ipath, irbb);
			var phb1 = util.Vec2.lc(hb1.p, 0.8, hb2.p, 0.2, hb1.norm, bondIdxOff);
			ipath = paper.text(phb1.x, phb1.y, bond.b.hb1.toString());
			irbb = rnd.relBox(ipath.getBBox());
			this.centerText(ipath, irbb);
			render.addItemPath(bond.visel, 'indices', ipath, irbb);
			var phb2 = util.Vec2.lc(hb1.p, 0.2, hb2.p, 0.8, hb1.norm, bondIdxOff);
			ipath = paper.text(phb2.x, phb2.y, bond.b.hb2.toString());
			irbb = rnd.relBox(ipath.getBBox());
			this.centerText(ipath, irbb);
			render.addItemPath(bond.visel, 'indices', ipath, irbb);
		} else if (opt.showLoopIds) {
			var pl1 = util.Vec2.lc(hb1.p, 0.5, hb2.p, 0.5, hb2.norm, bondIdxOff);
			ipath = paper.text(pl1.x, pl1.y, hb1.loop.toString());
			irbb = rnd.relBox(ipath.getBBox());
			this.centerText(ipath, irbb);
			render.addItemPath(bond.visel, 'indices', ipath, irbb);
			var pl2 = util.Vec2.lc(hb1.p, 0.5, hb2.p, 0.5, hb1.norm, bondIdxOff);
			ipath = paper.text(pl2.x, pl2.y, hb2.loop.toString());
			irbb = rnd.relBox(ipath.getBBox());
			this.centerText(ipath, irbb);
			render.addItemPath(bond.visel, 'indices', ipath, irbb);
		}
	}
};

rnd.ReStruct.prototype.labelIsVisible = function (aid, atom)
{
	if ((atom.a.neighbors.length < 2 && !this.render.opt.hideTerminalLabels) ||
		atom.a.label == null ||
		atom.a.label.toLowerCase() != "c" ||
		(atom.a.badConn && this.render.opt.showValenceWarnings) ||
		atom.a.isotope != 0 ||
		atom.a.radical != 0 ||
		atom.a.charge != 0 ||
		atom.a.explicitValence ||
		atom.a.atomList != null ||
        atom.a.rglabel != null)
		return true;
	if (!atom.showLabel && atom.a.neighbors.length == 2) {
		var n1 = atom.a.neighbors[0];
		var n2 = atom.a.neighbors[1];
		var hb1 = this.molecule.halfBonds.get(n1);
		var hb2 = this.molecule.halfBonds.get(n2);
		var b1 = this.bonds.get(hb1.bid);
		var b2 = this.bonds.get(hb2.bid);
		if (b1.b.type == b2.b.type && b1.b.stereo == chem.Struct.BOND.STEREO.NONE && b2.b.stereo == chem.Struct.BOND.STEREO.NONE)
			if (Math.abs(util.Vec2.cross(hb1.dir, hb2.dir)) < 0.05)
				return true;
	}
	return false;
};

rnd.ReStruct.prototype.checkLabelsToShow = function ()
{
	for (var aid in this.atomsChanged) {
		var atom = this.atoms.get(aid);
		atom.showLabel = this.labelIsVisible(aid, atom);
	}
};

rnd.ReStruct.layerMap = {
	'background' : 0,
	'selection-plate' : 1,
	'highlighting' : 2,
	'warnings' : 3,
	'data' : 4,
	'indices' : 5
};

rnd.ReStruct.prototype.addReObjectPath = function(group, visel, path) {
    var offset = this.render.offset;
    var bb = util.Box2Abs.fromRelBox(rnd.relBox(path.getBBox()));
    if (offset != null)
        path.translate(offset.x, offset.y);
    visel.add(path, bb);
    this.insertInLayer(rnd.ReStruct.layerMap[group], path);
};

/**  @deprecated please use #rnd.ReStruct.addReObjectPath instead */ // TODO code cleanup
rnd.ReStruct.prototype.addTmpPath = function (group, path)
{
	var visel = new rnd.Visel('TMP');
	var offset = this.render.offset;
	if (offset != null) {
		path.translate(offset.x, offset.y);
	}
	visel.add(path);
	this.tmpVisels.push(visel);
	this.insertInLayer(rnd.ReStruct.layerMap[group], path);
};

rnd.ReStruct.prototype.clearVisel = function (visel)
{
	for (var i = 0; i < visel.paths.length; ++i)
            visel.paths[i].remove();
	visel.clear();
};

rnd.ReStruct.prototype.shiftBonds = function ()
{
    var render = this.render;
	var settings = render.settings;
	for (var aid in this.atomsChanged) {
		var atom = this.atoms.get(aid);
		atom.a.neighbors.each( function (hbid) {
			var hb = this.molecule.halfBonds.get(hbid);
                        var ps = render.ps(atom.a.pp);
                        hb.p = this.shiftBondEnd(atom, ps, hb.dir, 2 * settings.lineWidth);
		}, this);
	}
};

rnd.ReStruct.prototype.selectDoubleBondShift = function (n1, n2, d1, d2) {
	if (n1 == 6 && n2 != 6 && (d1 > 1 || d2 == 1))
		return -1;
	if (n2 == 6 && n1 != 6 && (d2 > 1 || d1 == 1))
		return 1;
	if (n2 * d1 > n1 * d2)
		return -1;
	if (n2 * d1 < n1 * d2)
		return 1;
	if (n2 > n1)
		return -1;
	return 1;
};

rnd.ReStruct.prototype.selectDoubleBondShift_Chain = function (bond) {
	var struct = this.molecule;
	var hb1 = struct.halfBonds.get(bond.b.hb1);
	var hb2 = struct.halfBonds.get(bond.b.hb2);
	var nLeft = (hb1.leftSin > 0.3 ? 1 : 0) + (hb2.rightSin > 0.3 ? 1 : 0);
	var nRight = (hb2.leftSin > 0.3 ? 1 : 0) + (hb1.rightSin > 0.3 ? 1 : 0);
	if (nLeft > nRight)
		return -1;
	if (nLeft < nRight)
		return 1;
	if ((hb1.leftSin > 0.3 ? 1 : 0) + (hb1.rightSin > 0.3 ? 1 : 0) == 1)
		return 1;
	return 0;
};

rnd.ReStruct.prototype.setDoubleBondShift = function ()
{
	var struct = this.molecule;
	// double bonds in loops
	for (var bid in this.bondsChanged) {
		var bond = this.bonds.get(bid);
		var loop1, loop2;
		loop1 = struct.halfBonds.get(bond.b.hb1).loop;
		loop2 = struct.halfBonds.get(bond.b.hb2).loop;
		if (loop1 >= 0 && loop2 >= 0) {
			var d1 = struct.loops.get(loop1).dblBonds;
			var d2 = struct.loops.get(loop2).dblBonds;
			var n1 = struct.loops.get(loop1).hbs.length;
			var n2 = struct.loops.get(loop2).hbs.length;
			bond.doubleBondShift = this.selectDoubleBondShift(n1, n2, d1, d2);
		} else if (loop1 >= 0) {
			bond.doubleBondShift = -1;
		} else if (loop2 >= 0) {
			bond.doubleBondShift = 1;
		} else {
			bond.doubleBondShift = this.selectDoubleBondShift_Chain(bond);
		}
	}
};

rnd.ReStruct.prototype.updateLoops = function ()
{
	this.reloops.each(function(rlid, reloop){
		this.clearVisel(reloop.visel);
	}, this);
	this.findLoops();
};

rnd.ReStruct.prototype.renderLoops = function ()
{
    var render = this.render;
	var settings = render.settings;
	var paper = render.paper;
        var molecule = this.molecule;
	this.reloops.each(function(rlid, reloop){
		var loop = reloop.loop;
		reloop.centre = new util.Vec2();
		loop.hbs.each(function(hbid){
			var hb = molecule.halfBonds.get(hbid);
			var bond = this.bonds.get(hb.bid);
                        var apos = render.ps(this.atoms.get(hb.begin).a.pp);
			if (bond.b.type != chem.Struct.BOND.TYPE.AROMATIC)
				loop.aromatic = false;
			reloop.centre.add_(apos);
		}, this);
		loop.convex = true;
		for (var k = 0; k < reloop.loop.hbs.length; ++k)
		{
			var hba = molecule.halfBonds.get(loop.hbs[k]);
			var hbb = molecule.halfBonds.get(loop.hbs[(k + 1) % loop.hbs.length]);
			var angle = Math.atan2(
					util.Vec2.cross(hba.dir, hbb.dir),
					util.Vec2.dot(hba.dir, hbb.dir));
			if (angle > 0)
				loop.convex = false;
		}

		reloop.centre = reloop.centre.scaled(1.0 / loop.hbs.length);
		reloop.radius = -1;
		loop.hbs.each(function(hbid){
			var hb = molecule.halfBonds.get(hbid);
                        var apos = render.ps(this.atoms.get(hb.begin).a.pp);
			var bpos = render.ps(this.atoms.get(hb.end).a.pp);
			var n = util.Vec2.diff(bpos, apos).rotateSC(1, 0).normalized();
			var dist = util.Vec2.dot(util.Vec2.diff(apos, reloop.centre), n);
			if (reloop.radius < 0) {
				reloop.radius = dist;
			} else {
				reloop.radius = Math.min(reloop.radius, dist);
			}
		}, this);
		reloop.radius *= 0.7;
		if (!loop.aromatic)
			return;
		var path = null;
		if (loop.convex) {
			path = paper.circle(reloop.centre.x, reloop.centre.y, reloop.radius)
			.attr({
				'stroke': '#000',
				'stroke-width': settings.lineWidth
			});
		} else {
			var pathStr = '';
			for (k = 0; k < loop.hbs.length; ++k)
			{
				hba = molecule.halfBonds.get(loop.hbs[k]);
				hbb = molecule.halfBonds.get(loop.hbs[(k + 1) % loop.hbs.length]);
				angle = Math.atan2(
						util.Vec2.cross(hba.dir, hbb.dir),
						util.Vec2.dot(hba.dir, hbb.dir));
				var halfAngle = (Math.PI - angle) / 2;
				var dir = hbb.dir.rotate(halfAngle);
                                var pi = render.ps(this.atoms.get(hbb.begin).a.pp);
				var sin = Math.sin(halfAngle);
				var minSin = 0.1;
				if (Math.abs(sin) < minSin)
					sin = sin * minSin / Math.abs(sin);
				var offset = settings.bondSpace / sin;
				var qi = pi.addScaled(dir, -offset);
				pathStr += (k == 0 ? 'M' : 'L');
				pathStr += qi.x.toString() + ',' + qi.y.toString();
			}
			pathStr += 'Z';
			path = paper.path(pathStr)
			.attr({
				'stroke': '#000',
				'stroke-width': settings.lineWidth,
				'stroke-dasharray':'- '
			});
		}
		this.addReObjectPath('data', reloop.visel, path);
	}, this);
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.Prototype)
	throw new Error("Prototype.js should be loaded first");
if (!window.rnd || !rnd.ReStruct)
	throw new Error("rnd.MolData should be defined prior to loading this file");

rnd.DEBUG = false;

rnd.logcnt = 0;
rnd.logmouse = false;
rnd.hl = false;

/** @deprecated */
rnd.mouseEventNames = [
	'Click',
	'DblClick',
	'MouseOver',
	'MouseDown',
	'MouseMove',
	'MouseOut'
	];

rnd.logMethod = function () { };
//rnd.logMethod = function (method) {console.log("METHOD: " + method);}

rnd.RenderDummy = function (clientArea, scale, opt, viewSz)
{
	this.clientArea = clientArea = p$(clientArea);
	clientArea.innerHTML = "";
	this.paper = new Raphael(clientArea);
	this.paper.rect(0, 0, 100, 100).attr({
		'fill':'#0F0',
		'stroke':'none'
	});
	this.setMolecule = function(){};
	this.update = function(){};
};

rnd.Render = function (clientArea, scale, opt, viewSz)
{
	this.opt = opt || {};
	this.opt.showSelectionRegions = this.opt.showSelectionRegions || false;
	this.opt.showAtomIds = this.opt.showAtomIds || false;
	this.opt.showBondIds = this.opt.showBondIds || false;
	this.opt.showHalfBondIds = this.opt.showHalfBondIds || false;
	this.opt.showLoopIds = this.opt.showLoopIds || false;
	this.opt.showValenceWarnings = !Object.isUndefined(this.opt.showValenceWarnings) ? this.opt.showValenceWarnings : true;
	this.opt.autoScale = this.opt.autoScale || false;
	this.opt.autoScaleMargin = this.opt.autoScaleMargin || 0;
	this.opt.atomColoring = this.opt.atomColoring || 0;
	this.opt.hideImplicitHydrogen = this.opt.hideImplicitHydrogen || false;
	this.opt.hideTerminalLabels = this.opt.hideTerminalLabels || false;
	this.opt.ignoreMouseEvents = this.opt.ignoreMouseEvents || false;
	this.opt.selectionDistanceCoefficient = (this.opt.selectionDistanceCoefficient || 0.4) - 0;

	this.useOldZoom = Prototype.Browser.IE;
	this.scale = scale || 100;
	this.baseScale = this.scale;
	this.offset = new util.Vec2();
	this.clientArea = clientArea = p$(clientArea);
	clientArea.innerHTML = "";
	this.paper = new Raphael(clientArea);
	this.size = new util.Vec2();
	this.viewSz = viewSz || new util.Vec2(clientArea['clientWidth'] || 100, clientArea['clientHeight'] || 100);
	this.bb = new util.Box2Abs(new util.Vec2(), this.viewSz);
    /** @deprecated */
	this.curItem = {
		'type':'Canvas',
		'id':-1
	};
    /** @deprecated */
	this.pagePos = new util.Vec2();
    /** @deprecated */
	this.muteMouseOutMouseOver = false;
	this.dirty = true;
	this.selectionRect = null;
	this.rxnArrow = null;
	this.rxnMode = false;
	this.zoom = 1.0;

	var render = this;
	var valueT = 0, valueL = 0;
	var element = clientArea;
	do {
		valueT += element.offsetTop  || 0;
		valueL += element.offsetLeft || 0;
		element = element.offsetParent;
	} while (element);

	this.clientAreaPos = new util.Vec2(valueL, valueT);

    // rbalabanov: two-fingers scrolling & zooming for iPad
    // TODO should be moved to touch.js module, re-factoring needed
    //BEGIN
    clientArea.observe('touchstart', function(event) {
        if (event.touches.length == 2) {
            this._tui = this._tui || {};
            this._tui.center = {
                pageX : (event.touches[0].pageX + event.touches[1].pageX) / 2,
                pageY : (event.touches[0].pageY + event.touches[1].pageY) / 2
            };
            ui.setZoomStaticPointInit(ui.page2obj(this._tui.center));
        }
    });
    clientArea.observe('touchmove', function(event) {
        if ('_tui' in this && event.touches.length == 2) {
            this._tui.center = {
                pageX : (event.touches[0].pageX + event.touches[1].pageX) / 2,
                pageY : (event.touches[0].pageY + event.touches[1].pageY) / 2
            };
        }
    });
    clientArea.observe('gesturestart', function(event) {
        this._tui = this._tui || {};
        this._tui.scale0 = ui.render.zoom;
        event.preventDefault();
    });
    clientArea.observe('gesturechange', function(event) {
        ui.setZoomStaticPoint(this._tui.scale0 * event.scale, ui.page2canvas2(this._tui.center));
        ui.render.update();
        event.preventDefault();
    });
    clientArea.observe('gestureend', function(event) {
        delete this._tui;
        event.preventDefault();
    });
    //END

	clientArea.observe('onresize', function(event) {
        render.onResize();
    });

    // rbalabanov: here is temporary fix for "drag issue" on iPad
    //BEGIN
    if ('hiddenPaths' in rnd.ReStruct.prototype) {
        clientArea.observe('touchend', function(event) {
            if (event.touches.length == 0) {
                while (rnd.ReStruct.prototype.hiddenPaths.length > 0) rnd.ReStruct.prototype.hiddenPaths.pop().remove();
            }
        });
    }
    //END

    //rbalabanov: temporary
    //BEGIN
    clientArea.observe('mouseup', function(event) {
        ui.render.current_tool && ui.render.current_tool.processEvent('OnMouseUp', event);
    });
    clientArea.observe('touchend', function(event) {
        if (event.touches.length == 0) {
            ui.render.current_tool && ui.render.current_tool.processEvent('OnMouseUp', new rnd.MouseEvent(event));
        }
    });
    //END

	if (!this.opt.ignoreMouseEvents) {
		// assign canvas events handlers
		rnd.mouseEventNames.each(function(eventName){
            var bindEventName = eventName.toLowerCase();
            bindEventName = EventMap[bindEventName] || bindEventName;
			clientArea.observe(bindEventName, function(event) {
                if (!ui || !ui.is_touch) {
                    // TODO: karulin: fix this on touch devices if needed
                    var co = clientArea.cumulativeOffset();
                    co = new util.Vec2(co[0], co[1]);
                    var vp = new util.Vec2(event.clientX, event.clientY).sub(co);
                    var sz = new util.Vec2(clientArea.clientWidth, clientArea.clientHeight);
                    if (!(vp.x > 0 && vp.y > 0 && vp.x < sz.x && vp.y < sz.y)) // ignore events on the hidden part of the canvas
                        return util.preventDefault(event);
                }

                var ntHandled = ui.render.current_tool && ui.render.current_tool.processEvent('On' + eventName, event);
                var name = '_onCanvas' + eventName;
				if (!(ui.render.current_tool) && (!('touches' in event) || event.touches.length == 1) && render[name])
					render[name](new rnd.MouseEvent(event));
				util.stopEventPropagation(event);
                if (bindEventName != 'touchstart' && (bindEventName != 'touchmove' || event.touches.length != 2))
                    return util.preventDefault(event);
			});
		}, this);
	}

	this.ctab = new rnd.ReStruct(new chem.Struct(), this);
	this.settings = null;
	this.styles = null;
    /** @deprecated */
	this.checkCurItem = true;

	this.onCanvasOffsetChanged = null; //function(newOffset, oldOffset){};
	this.onCanvasSizeChanged = null; //function(newSize, oldSize){};
};

/**
 *
 * @param type
 * @param id
 * @param event
 * @deprecated
 */
rnd.Render.prototype.setCurrentItem = function (type, id, event) {
    if (this.current_tool) return;
	var oldType = this.curItem.type, oldId = this.curItem.id;
	if (type != oldType || id != oldId) {
		this.curItem = {
			'type':type,
			'id':id
		};
		if (oldType == 'Canvas'
			|| (oldType == 'Atom' && this.ctab.atoms.has(oldId))
			|| (oldType == 'RxnArrow' && this.ctab.rxnArrows.has(oldId))
			|| (oldType == 'RxnPlus' && this.ctab.rxnPluses.has(oldId))
			|| (oldType == 'Bond' && this.ctab.bonds.has(oldId))
			|| (oldType == 'SGroup' && this.ctab.sgroups.has(oldId))) {
			this.callEventHandler(event, 'MouseOut', oldType, oldId);
		}
		this.callEventHandler(event, 'MouseOver', type, id);
	}
};

rnd.Render.prototype.view2scaled = function (p, isRelative) {
	if (!this.useOldZoom)
		p = p.scaled(1/this.zoom);
	p = isRelative ? p : p.add(ui.scrollPos().scaled(1/this.zoom)).sub(this.offset);
	return p;
};

rnd.Render.prototype.scaled2view = function (p, isRelative) {
	p = isRelative ? p : p.add(this.offset).sub(ui.scrollPos().scaled(1/this.zoom));
	if (!this.useOldZoom)
		p = p.scaled(this.zoom);
	return p;
};

rnd.Render.prototype.scaled2obj = function (v) {
	return v.scaled(1 / this.settings.scaleFactor);
};

rnd.Render.prototype.obj2scaled = function (v) {
	return v.scaled(this.settings.scaleFactor);
};

rnd.Render.prototype.view2obj = function (v, isRelative) {
	return this.scaled2obj(this.view2scaled(v, isRelative));
};

rnd.Render.prototype.obj2view = function (v, isRelative) {
	return this.scaled2view(this.obj2scaled(v, isRelative));
};

/**
 *
 * @param event
 * @deprecated
 */
rnd.Render.prototype.checkCurrentItem = function (event) {
    if (this.current_tool) return;
	if (this.offset) {
		this.pagePos = new util.Vec2(event.pageX, event.pageY);
		var clientPos = null;
		if ('ui' in window && 'page2obj' in ui) // TODO: the render shouldn't be aware of the page coordinates
			clientPos = new util.Vec2(ui.page2obj(event));
		else
			clientPos = this.pagePos.sub(this.clientAreaPos);
		var item = this.findClosestItem(clientPos);
		this.setCurrentItem(item.type, item.id, event);
	}
};

rnd.Render.prototype.findItem = function(event, maps, skip) {
    var ci = this.findClosestItem(
        'ui' in window && 'page2obj' in ui
            ? new util.Vec2(ui.page2obj(event))
            : new util.Vec2(event.pageX, event.pageY).sub(this.clientAreaPos),
        maps,
        skip
    );
    //rbalabanov: let it be this way at the moment
    if (ci.type == 'Atom') ci.map = 'atoms';
    else if (ci.type == 'Bond') ci.map = 'bonds';
    else if (ci.type == 'SGroup') ci.map = 'sgroups';
    else if (ci.type == 'DataSGroupData') ci.map = 'sgroupData';
    else if (ci.type == 'RxnArrow') ci.map = 'rxnArrows';
    else if (ci.type == 'RxnPlus') ci.map = 'rxnPluses';
    else if (ci.type == 'Fragment') ci.map = 'frags';
    else if (ci.type == 'RGroup') ci.map = 'rgroups';
    else if (ci.type == 'ChiralFlag') ci.map = 'chiralFlags';
    return ci;
};

rnd.Render.prototype.client2Obj = function (clientPos) {
	return new util.Vec2(clientPos).sub(this.offset);
};

/**
 *
 * @param event
 * @param eventName
 * @param type
 * @param id
 * @deprecated
 */
rnd.Render.prototype.callEventHandler = function (event, eventName, type, id) {
    if (this.current_tool) return;
	var name = 'on' + type + eventName;
	var handled = false;
	if (this[name])
		handled = this[name](event, id);
	if (!handled && type != 'Canvas') {
		var name1 = 'onCanvas' + eventName;
		if (this[name1])
			handled = this[name1](event);
	}
};

util.each(['MouseMove','MouseDown','MouseUp','Click','DblClick'],
	function(eventName) {
		rnd.Render.prototype['_onCanvas' + eventName] = function(event){
			this.checkCurrentItem(event);
			this.callEventHandler(event, eventName, this.curItem.type, this.curItem.id);
		}
	}
);

rnd.Render.prototype.setMolecule = function (ctab, norescale)
{
	rnd.logMethod("setMolecule");
	this.paper.clear();
	this.ctab = new rnd.ReStruct(ctab, this, norescale);
	this.offset = null;
	this.size = null;
	this.bb = null;
	this.rxnMode = ctab.isReaction;
};

// molecule manipulation interface
rnd.Render.prototype.atomGetAttr = function (aid, name)
{
	rnd.logMethod("atomGetAttr");
	// TODO: check attribute names
	return this.ctab.molecule.atoms.get(aid)[name];
};

rnd.Render.prototype.invalidateAtom = function (aid, level)
{
	var atom = this.ctab.atoms.get(aid);
	this.ctab.markAtom(aid, level ? 1 : 0);
	var hbs = this.ctab.molecule.halfBonds;
	for (var i = 0; i < atom.a.neighbors.length; ++i) {
		var hbid = atom.a.neighbors[i];
		if (hbs.has(hbid)) {
			var hb = hbs.get(hbid);
			this.ctab.markBond(hb.bid, 1);
			this.ctab.markAtom(hb.end, 0);
		}
	}
};

rnd.Render.prototype.invalidateBond = function (bid, invalidateLoops)
{
	var bond = this.ctab.bonds.get(bid);
	this.invalidateAtom(bond.b.begin, 0);
	this.invalidateAtom(bond.b.end, 0);
	if (invalidateLoops) {
		var lid1 = this.ctab.molecule.halfBonds.get(bond.b.hb1).loop;
		var lid2 = this.ctab.molecule.halfBonds.get(bond.b.hb2).loop;
		if (lid1 >= 0)
			this.ctab.loopRemove(lid1);
		if (lid2 >= 0)
			this.ctab.loopRemove(lid2);
	}
};

rnd.Render.prototype.invalidateItem = function (map, id, level)
{
	if (map == 'atoms')
		this.invalidateAtom(id, level);
	else if (map == 'bonds')
		this.invalidateBond(id, level);
	else
		this.ctab.markItem(map, id, level);
};

rnd.Render.prototype.atomGetDegree = function (aid)
{
	rnd.logMethod("atomGetDegree");
	return this.ctab.atoms.get(aid).a.neighbors.length;
};

rnd.Render.prototype.isBondInRing = function (bid) {
	var bond = this.ctab.bonds.get(bid);
	return this.ctab.molecule.halfBonds.get(bond.b.hb1).loop >= 0 ||
	this.ctab.molecule.halfBonds.get(bond.b.hb2).loop >= 0;
};

rnd.Render.prototype.atomGetNeighbors = function (aid)
{
	var atom = this.ctab.atoms.get(aid);
	var neiAtoms = [];
	for (var i = 0; i < atom.a.neighbors.length; ++i) {
		var hb = this.ctab.molecule.halfBonds.get(atom.a.neighbors[i]);
		neiAtoms.push({
			'aid': hb.end - 0,
			'bid': hb.bid - 0
		});
	}
	return neiAtoms;
};

// returns an array of s-group id's
rnd.Render.prototype.atomGetSGroups = function (aid)
{
	rnd.logMethod("atomGetSGroups");
	var atom = this.ctab.atoms.get(aid);
	return util.Set.list(atom.a.sgs);
};

rnd.Render.prototype.sGroupGetAttr = function (sgid, name)
{
	rnd.logMethod("sGroupGetAttr");
	return this.ctab.sgroups.get(sgid).item.getAttr(name);
};

rnd.Render.prototype.sGroupGetAttrs = function (sgid)
{
	rnd.logMethod("sGroupGetAttrs");
	return this.ctab.sgroups.get(sgid).item.getAttrs();
};

// TODO: move to SGroup
rnd.Render.prototype.sGroupGetAtoms = function (sgid)
{
	rnd.logMethod("sGroupGetAtoms");
	var sg = this.ctab.sgroups.get(sgid).item;
	return chem.SGroup.getAtoms(this.ctab.molecule, sg);
};

rnd.Render.prototype.sGroupGetType = function (sgid)
{
	rnd.logMethod("sGroupGetType");
	var sg = this.ctab.sgroups.get(sgid).item;
	return sg.type;
};

rnd.Render.prototype.sGroupsFindCrossBonds = function ()
{
	rnd.logMethod("sGroupsFindCrossBonds");
	this.ctab.molecule.sGroupsRecalcCrossBonds();
};

// TODO: move to ReStruct
rnd.Render.prototype.sGroupGetNeighborAtoms = function (sgid)
{
	rnd.logMethod("sGroupGetNeighborAtoms");
	var sg = this.ctab.sgroups.get(sgid).item;
	return sg.neiAtoms;
};

// TODO: move to ReStruct
rnd.Render.prototype.atomIsPlainCarbon = function (aid)
{
	rnd.logMethod("atomIsPlainCarbon");
	return this.ctab.atoms.get(aid).a.isPlainCarbon();
};

rnd.Render.prototype.highlightObject = function(obj, visible) {
    if (['atoms', 'bonds', 'rxnArrows', 'rxnPluses', 'chiralFlags', 'frags', 'rgroups', 'sgroups', 'sgroupData'].indexOf(obj.map) > -1) {
        var item = this.ctab[obj.map].get(obj.id);
        if (item == null)
            return true; // TODO: fix, attempt to highlight a deleted item
        if ((obj.map == 'sgroups' && item.item.type == 'DAT') || obj.map == 'sgroupData') {
            // set highlight for both the group and the data item
            var item1 = this.ctab.sgroups.get(obj.id);
            var item2 = this.ctab.sgroupData.get(obj.id);
            if (item1 != null)
                item1.setHighlight(visible, this);
            if (item2 != null)
                item2.setHighlight(visible, this);
        } else {
            item.setHighlight(visible, this);
        }
    } else {
        return false;
    }
    return true;
};

rnd.Render.prototype.itemGetPos = function (map, id)
{
    return this.ctab.molecule[map].get(id).pp;
};

rnd.Render.prototype.atomGetPos = function (id)
{
	rnd.logMethod("atomGetPos");
	return this.itemGetPos('atoms', id);
};

rnd.Render.prototype.rxnArrowGetPos = function (id)
{
	rnd.logMethod("rxnArrowGetPos");
	return this.itemGetPos('rxnArrows', id);
};

rnd.Render.prototype.rxnPlusGetPos = function (id)
{
	rnd.logMethod("rxnPlusGetPos");
	return this.itemGetPos('rxnPluses', id);
};

rnd.Render.prototype.getAdjacentBonds = function (atoms) {
	var aidSet = util.Set.fromList(atoms);
	var bidSetInner = util.Set.empty(), bidSetCross = util.Set.empty();
	for (var i = 0; i < atoms.length; ++i) {
		var aid = atoms[i];
		var atom = this.ctab.atoms.get(aid);
		for (var j = 0; j < atom.a.neighbors.length; ++j) {
			var hbid = atom.a.neighbors[j];
			var hb = this.ctab.molecule.halfBonds.get(hbid);
			var endId = hb.end;
			var set = util.Set.contains(aidSet, endId) ?
				bidSetInner : bidSetCross;
			util.Set.add(set, hb.bid);
		}
	}
	return {'inner': bidSetInner, 'cross': bidSetCross};
};

rnd.Render.prototype.bondGetAttr = function (bid, name)
{
	rnd.logMethod("bondGetAttr");
	return this.ctab.bonds.get(bid).b[name];
};

rnd.Render.prototype.setSelection = function (selection)
{
	rnd.logMethod("setSelection");
	for (var map in rnd.ReStruct.maps) {
            if (map == 'frags' || map == 'rgroups')
                continue;

        var set = selection ? (selection[map] ? util.identityMap(selection[map]) : {}) : null;
		this.ctab[map].each(function(id, item){
            var selected = set ? set[id] === id : item.selected;
			item.selected = selected;
			this.ctab.showItemSelection(id, item, selected);
		}, this);
	}
};

rnd.Render.prototype.initStyles = function ()
{
	// TODO move fonts, dashed lines, etc. here
	var settings = this.settings;
	this.styles = {};
	this.styles.lineattr = {
		stroke: '#000',
		'stroke-width': settings.lineWidth,
		'stroke-linecap' : 'round',
		'stroke-linejoin' : 'round'
	};
	this.styles.selectionStyle = {
		'fill':'#7f7',
		'stroke':'none'
	};
	this.styles.selectionZoneStyle = {
		'fill':'#000',
		'stroke':'none',
		'opacity':0.0
	};
	this.styles.highlightStyle = {
		'stroke':'#0c0',
		'stroke-width':0.6*settings.lineWidth
		};
	this.styles.sGroupHighlightStyle = {
		'stroke':'#9900ff',
		'stroke-width':0.6*settings.lineWidth
		};
	this.styles.sgroupBracketStyle = {
		'stroke':'darkgray',
		'stroke-width':0.5*settings.lineWidth
		};
	this.styles.atomSelectionPlateRadius = settings.labelFontSize * 1.2 ;
};

rnd.Render.prototype.initSettings = function()
{
	var settings = this.settings = {};
	settings.delta = this.ctab.molecule.getCoordBoundingBox();
	settings.margin = 0.1;
	settings.scaleFactor = this.scale;
	settings.lineWidth = settings.scaleFactor / 20;
	settings.bondShift = settings.scaleFactor / 6;
	settings.bondSpace = settings.scaleFactor / 7;
	settings.labelFontSize = Math.ceil(1.9 * (settings.scaleFactor / 6)); // TODO: don't round?
	settings.subFontSize = Math.ceil(0.7 * settings.labelFontSize);
	// font size is not determined by the number in this string,
	//  but by the 'font-size' property
	settings.font = '30px "Arial"';
	settings.fontsz = this.settings.labelFontSize;
	settings.fontszsub = this.settings.subFontSize;
	settings.fontRLabel = this.settings.labelFontSize * 1.2;
	settings.fontRLogic = this.settings.labelFontSize * 0.7;
};

rnd.Render.prototype.getBoundingBox = function ()
{
	var bb = null, vbb;
	this.ctab.eachVisel(function(visel){
		vbb = visel.boundingBox;
		if (vbb)
			bb = bb ? util.Box2Abs.union(bb, vbb) : vbb.clone();
	}, this);
	if (!bb)
		bb = new util.Box2Abs(0, 0, 0, 0);
	return bb;
};

rnd.Render.prototype.getStructCenter = function ()
{
	var bb = this.getBoundingBox();
	return this.scaled2obj(util.Vec2.lc2(bb.p0, 0.5, bb.p1, 0.5));
};

rnd.Render.prototype.onResize = function ()
{
	this.setViewSize(new util.Vec2(this.clientArea['clientWidth'], this.clientArea['clientHeight']));
};

rnd.Render.prototype.setViewSize = function (viewSz)
{
     this.viewSz = new util.Vec2(viewSz);
};

rnd.Render.prototype._setPaperSize = function (sz)
{
	var z = this.zoom;
	this.paper.setSize(sz.x * z, sz.y * z);
	this.setViewBox(z);
};

rnd.Render.prototype.setPaperSize = function (sz)
{
	rnd.logMethod("setPaperSize");
	var oldSz = this.sz;
	this.sz = sz;
	this._setPaperSize(sz);
	if (this.onCanvasSizeChanged)
		this.onCanvasSizeChanged(sz, oldSz);
};

rnd.Render.prototype.setOffset = function (offset)
{
	rnd.logMethod("setOffset");
	var oldOffset = this.offset;
	this.offset = offset;
	if (this.onCanvasOffsetChanged)
		this.onCanvasOffsetChanged(offset, oldOffset);
};

rnd.Render.prototype.getElementPos = function (obj)
{
	var curleft = 0, curtop = 0;

	if (obj.offsetParent) {
		do {
			curleft += obj.offsetLeft;
			curtop += obj.offsetTop;
		} while ((obj = obj.offsetParent));
	}
	return new util.Vec2(curleft,curtop);
};

rnd.Render.prototype.drawSelectionLine = function (p0, p1) {
	rnd.logMethod("drawSelectionLine");
	if (this.selectionRect) {
		this.selectionRect.remove();
	    this.selectionRect = null;
    }
	if (p0 && p1) {
		p0 = this.obj2scaled(p0).add(this.offset);
		p1 = this.obj2scaled(p1).add(this.offset);
		this.selectionRect = this.paper.path(
            'M' + p0.x.toString() + ',' + p0.y.toString() + 'L' + p1.x.toString() + ',' + p1.y.toString()
        ).attr({'stroke':'gray', 'stroke-width':'1px'});
	}
};

rnd.Render.prototype.drawSelectionRectangle = function (p0, p1) {
	rnd.logMethod("drawSelectionRectangle");
	if (this.selectionRect) {
		this.selectionRect.remove();
	    this.selectionRect = null;
    }
	if (p0 && p1) {
		p0 = this.obj2scaled(p0).add(this.offset);
		p1 = this.obj2scaled(p1).add(this.offset);
		this.selectionRect = this.paper.rect(
            Math.min(p0.x, p1.x), Math.min(p0.y, p1.y), Math.abs(p1.x - p0.x), Math.abs(p1.y - p0.y)
        ).attr({'stroke':'gray', 'stroke-width':'1px'});
	}
};

rnd.Render.prototype.getElementsInRectangle = function (p0,p1) {
	rnd.logMethod("getElementsInRectangle");
	var bondList = new Array();
	var atomList = new Array();

	var x0 = Math.min(p0.x, p1.x), x1 = Math.max(p0.x, p1.x), y0 = Math.min(p0.y, p1.y), y1 = Math.max(p0.y, p1.y);
	this.ctab.bonds.each(function (bid, bond){
		var centre = util.Vec2.lc2(this.ctab.atoms.get(bond.b.begin).a.pp, 0.5,
			this.ctab.atoms.get(bond.b.end).a.pp, 0.5);
		if (centre.x > x0 && centre.x < x1 && centre.y > y0 && centre.y < y1)
			bondList.push(bid);
	}, this);
	this.ctab.atoms.each(function(aid, atom) {
		if (atom.a.pp.x > x0 && atom.a.pp.x < x1 && atom.a.pp.y > y0 && atom.a.pp.y < y1)
			atomList.push(aid);
	}, this);
	var rxnArrowsList = new Array();
	var rxnPlusesList = new Array();
	this.ctab.rxnArrows.each(function(id, item){
		if (item.item.pp.x > x0 && item.item.pp.x < x1 && item.item.pp.y > y0 && item.item.pp.y < y1)
			rxnArrowsList.push(id);
	}, this);
	this.ctab.rxnPluses.each(function(id, item){
		if (item.item.pp.x > x0 && item.item.pp.x < x1 && item.item.pp.y > y0 && item.item.pp.y < y1)
			rxnPlusesList.push(id);
	}, this);
	var chiralFlagList = new Array();
	this.ctab.chiralFlags.each(function(id, item){
		if (item.pp.x > x0 && item.pp.x < x1 && item.pp.y > y0 && item.pp.y < y1)
			chiralFlagList.push(id);
	}, this);
	var sgroupDataList = new Array();
	this.ctab.sgroupData.each(function(id, item){
		if (item.sgroup.pp.x > x0 && item.sgroup.pp.x < x1 && item.sgroup.pp.y > y0 && item.sgroup.pp.y < y1)
			sgroupDataList.push(id);
	}, this);
	return {
		'atoms':atomList,
		'bonds':bondList,
		'rxnArrows':rxnArrowsList,
		'rxnPluses':rxnPlusesList,
		'chiralFlags':chiralFlagList,
		'sgroupData':sgroupDataList
	};
};

rnd.Render.prototype.drawSelectionPolygon = function (r) {
	rnd.logMethod("drawSelectionPolygon");
	if (this.selectionRect) {
		this.selectionRect.remove();
	    this.selectionRect = null;
    }
	if (r && r.length > 1) {
		var v = this.obj2scaled(r[r.length - 1]).add(this.offset);
		var pstr = "M" + v.x.toString() + "," + v.y.toString();
		for (var i = 0; i < r.length; ++i) {
			v = this.obj2scaled(r[i]).add(this.offset);
			pstr += "L" + v.x.toString() + "," + v.y.toString();
		}
		this.selectionRect = this.paper.path(pstr).attr({'stroke':'gray', 'stroke-width':'1px'});
	}
};

rnd.Render.prototype.isPointInPolygon = function (r, p) {
	var d = new util.Vec2(0, 1);
	var n = d.rotate(Math.PI/2);
	var v0 = util.Vec2.diff(r[r.length - 1], p);
	var n0 = util.Vec2.dot(n, v0);
	var d0 = util.Vec2.dot(d, v0);
	var w0 = null;
	var counter = 0;
	var eps = 1e-5;
	var flag1 = false, flag0 = false;

	for (var i = 0; i < r.length; ++i) {
		var v1 = util.Vec2.diff(r[i], p);
		var w1 = util.Vec2.diff(v1, v0);
		var n1 = util.Vec2.dot(n, v1);
		var d1 = util.Vec2.dot(d, v1);
		flag1 = false;
		if (n1 * n0 < 0)
		{
			if (d1 * d0 > -eps) {
				if (d0 > -eps)
					flag1 = true;
			} else if ((Math.abs(n0) * Math.abs(d1) - Math.abs(n1) * Math.abs(d0)) * d1 > 0) {
				flag1 = true;
			}
		}
		if (flag1 && flag0 && util.Vec2.dot(w1, n) * util.Vec2(w0, n) >= 0)
			flag1 = false;
		if (flag1)
			counter++;
		v0 = v1;
		n0 = n1;
		d0 = d1;
		w0 = w1;
		flag0 = flag1;
	}
	return (counter % 2) != 0;
};

rnd.Render.prototype.ps = function (pp) {
    return pp.scaled(this.settings.scaleFactor);
}

rnd.Render.prototype.getElementsInPolygon = function (rr) {
	rnd.logMethod("getElementsInPolygon");
	var bondList = new Array();
	var atomList = new Array();
	var r = [];
	for (var i = 0; i < rr.length; ++i) {
		r[i] = new util.Vec2(rr[i].x, rr[i].y);
	}
	this.ctab.bonds.each(function (bid, bond){
		var centre = util.Vec2.lc2(this.ctab.atoms.get(bond.b.begin).a.pp, 0.5,
			this.ctab.atoms.get(bond.b.end).a.pp, 0.5);
		if (this.isPointInPolygon(r, centre))
			bondList.push(bid);
	}, this);
	this.ctab.atoms.each(function(aid, atom){
		if (this.isPointInPolygon(r, atom.a.pp))
			atomList.push(aid);
	}, this);
	var rxnArrowsList = new Array();
	var rxnPlusesList = new Array();
	this.ctab.rxnArrows.each(function(id, item){
		if (this.isPointInPolygon(r, item.item.pp))
			rxnArrowsList.push(id);
	}, this);
	this.ctab.rxnPluses.each(function(id, item){
		if (this.isPointInPolygon(r, item.item.pp))
			rxnPlusesList.push(id);
	}, this);
	var chiralFlagList = new Array();
	this.ctab.chiralFlags.each(function(id, item){
		if (this.isPointInPolygon(r, item.pp))
			chiralFlagList.push(id);
	}, this);
	var sgroupDataList = new Array();
	this.ctab.sgroupData.each(function(id, item){
		if (this.isPointInPolygon(r, item.sgroup.pp))
			sgroupDataList.push(id);
	}, this);

	return {
		'atoms':atomList,
		'bonds':bondList,
		'rxnArrows':rxnArrowsList,
		'rxnPluses':rxnPlusesList,
		'chiralFlags':chiralFlagList,
		'sgroupData':sgroupDataList
	};
};

rnd.Render.prototype.testPolygon = function (rr) {
	rr = rr || [
	{
		x:50,
		y:10
	},

	{
		x:20,
		y:90
	},

	{
		x:90,
		y:30
	},

	{
		x:10,
		y:30
	},

	{
		x:90,
		y:80
	}
	];
	if (rr.length < 3)
		return;
	var min = rr[0], max = rr[0];
	for (var j = 1; j < rr.length; ++j) {
		min = util.Vec2.min(min, rr[j]);
		max = util.Vec2.max(max, rr[j]);
	}
	this.drawSelectionPolygon(rr);
	for (var k = 0; k < 1000; ++k) {
		var p = new util.Vec2(Math.random() * zz, Math.random() * zz);
		var isin = this.isPointInPolygon(rr, p);
		var color = isin ? '#0f0' : '#f00';
		this.paper.circle(p.x, p.y, 2).attr({
			'fill':color,
			'stroke':'none'
		});
	}
	this.drawSelectionPolygon(rr);
};

/**
 *
 * @param action
 * @param args
 * @deprecated
 */
rnd.Render.prototype.processAction = function (action, args)
{
	var id = parseInt(args[0]);
	if (action == 'atomRemove' && this.curItem.type == 'Atom'
		&& this.curItem.id == id && this._onAtomMouseOut) {
		this._onAtomMouseOut({
			'pageX':this.pagePos.x,
			'pageY':this.pagePos.y
			},
		this.curItem.id);
	}
	if (action == 'bondRemove' && this.curItem.type == 'Bond'
		&& this.curItem.id == id && this._onBondMouseOut) {
		this._onBondMouseOut({
			'pageX':this.pagePos.x,
			'pageY':this.pagePos.y
			},
		this.curItem.id);
	}
	if (action == 'rxnArrowRemove' && this.curItem.type == 'RxnArrow'
		&& this.curItem.id == id && this._onRxnArrowMouseOut) {
		this._onRxnArrowMouseOut({
			'pageX':this.pagePos.x,
			'pageY':this.pagePos.y
			},
		this.curItem.id);
	}
	if (action == 'rxnPlusRemove' && this.curItem.type == 'RxnPlus'
		&& this.curItem.id == id && this._onRxnPlusMouseOut) {
		this.onRxnArrowMouseOut({
			'pageX':this.pagePos.x,
			'pageY':this.pagePos.y
			},
		this.curItem.id);
	}
	this.muteMouseOutMouseOver = true;
	var ret = this['_' + action].apply(this, args);
	this.muteMouseOutMouseOver = false;
	if (action.endsWith('Add'))
		this.checkCurItem = true;
	return ret;
};

rnd.Render.prototype.update = function (force)
{
	rnd.logMethod("update");
	this.muteMouseOutMouseOver = true;

	if (!this.settings || this.dirty) {
		if (this.opt.autoScale)
		{
			var cbb = this.ctab.molecule.getCoordBoundingBox();
			// this is only an approximation to select some scale that's close enough to the target one
			var sy = cbb.max.y - cbb.min.y > 0 ? this.viewSz.y / (cbb.max.y - cbb.min.y) : 100;
			var sx = cbb.max.x - cbb.min.x > 0 ? this.viewSz.x / (cbb.max.x - cbb.min.x) : 100;
			this.scale = Math.max(sy, sx);
		}
		this.initSettings();
		this.initStyles();
		this.dirty = false;
		force = true;
	}

	var start = (new Date).getTime();
	var changes = this.ctab.update(force);
    this.setSelection(null); // [MK] redraw the selection bits where necessary
	var time = (new Date).getTime() - start;
	if (force && p$('log'))
		p$('log').innerHTML = time.toString() + '\n';
	if (changes) {
		var sf = this.settings.scaleFactor;
		var bb = this.getBoundingBox();

		if (!this.opt.autoScale) {
			var ext = util.Vec2.UNIT.scaled(sf);
			bb = bb.extend(ext, ext);
			if (this.bb)
				this.bb = util.Box2Abs.union(this.bb, bb);
			else
			{
				var d = this.viewSz.sub(bb.sz()).scaled(0.5).max(util.Vec2.ZERO);
				this.bb = bb.extend(d, d);
			}
			bb = this.bb.clone();

			var sz = util.Vec2.max(bb.sz().floor(), this.viewSz);
			var offset = bb.p0.negated().ceil();
			if (!this.sz || sz.x > this.sz.x || sz.y > this.sz.y)
				this.setPaperSize(sz);

			var oldOffset = this.offset || new util.Vec2();
			var delta = offset.sub(oldOffset);
			if (!this.offset || delta.x > 0 || delta.y > 0) {
				this.setOffset(offset);
				this.ctab.translate(delta);
				this.bb.translate(delta);
			}
		} else {
			var sz1 = bb.sz();
			var marg = new util.Vec2(this.opt.autoScaleMargin, this.opt.autoScaleMargin);
			var csz = this.viewSz.sub(marg.scaled(2));
			if (csz.x < 1 || csz.y < 1)
				throw new Error("View box too small for the given margin");
			var rescale = Math.min(csz.x / sz1.x, csz.y / sz1.y);
			this.ctab.scale(rescale);
			var offset1 = csz.sub(sz1.scaled(rescale)).scaled(0.5).add(marg).sub(bb.pos().scaled(rescale));
			this.ctab.translate(offset1);
		}
	}

    /** @deprecated */
	this.muteMouseOutMouseOver = false;
	if (this.checkCurItem) {
		this.checkCurItem = false;
		var event = new rnd.MouseEvent({
			'pageX':this.pagePos.x,
			'pageY':this.pagePos.y
			});
		this.checkCurrentItem(event);
	}
};

rnd.Render.prototype.checkBondExists = function (begin, end) {
	return this.ctab.molecule.checkBondExists(begin, end);
};

rnd.Render.prototype.findClosestAtom = function (pos, minDist, skip) { // TODO should be a member of ReAtom (see ReFrag)
	var closestAtom = null;
	var maxMinDist = this.opt.selectionDistanceCoefficient;
	minDist = minDist || maxMinDist;
	minDist	= Math.min(minDist, maxMinDist);
	this.ctab.atoms.each(function(aid, atom){
        if (aid != skip) {
            var dist = util.Vec2.dist(pos, atom.a.pp);
            if (dist < minDist) {
                closestAtom = aid;
                minDist = dist;
            }
        }
	}, this);
	if (closestAtom != null)
		return {
			'id':closestAtom,
			'dist':minDist
		};
	return null;
};

rnd.Render.prototype.findClosestBond = function (pos, minDist) { // TODO should be a member of ReBond (see ReFrag)
	var closestBond = null;
	var maxMinDist = this.opt.selectionDistanceCoefficient;
	minDist = minDist || maxMinDist;
	minDist = Math.min(minDist, maxMinDist);
	this.ctab.bonds.each(function(bid, bond){
		var hb = this.ctab.molecule.halfBonds.get(bond.b.hb1);
		var d = hb.dir;
		var n = hb.norm;
		var p1 = this.ctab.atoms.get(bond.b.begin).a.pp,
		p2 = this.ctab.atoms.get(bond.b.end).a.pp;

		var inStripe = util.Vec2.dot(pos.sub(p1),d) * util.Vec2.dot(pos.sub(p2),d) < 0;
		if (inStripe) {
			var dist = Math.abs(util.Vec2.dot(pos.sub(p1),n));
			if (dist < minDist) {
				closestBond = bid;
				minDist = dist;
			}
		}
	}, this);
	if (closestBond != null)
		return {
			'id':closestBond,
			'dist':minDist
		};
	return null;
};

rnd.Render.prototype.findClosestItem = function (pos, maps, skip) {
	var ret = null;
	var updret = function(type, item) {
		if (item != null && (ret == null || ret.dist > item.dist)) {
			ret = {
				'type':type,
				'id':item.id,
				'dist':item.dist
			};
		}
	};

    // TODO make it "map-independent", each object should be able to "report" its distance to point (something like ReAtom.dist(point))
    if (!maps || maps.indexOf('atoms') >= 0) {
        var atom = this.findClosestAtom(
            pos, undefined, !Object.isUndefined(skip) && skip.map == 'atoms' ? skip.id : undefined
        );
        updret('Atom', atom);
    }
    if (!maps || maps.indexOf('bonds') >= 0) {
        var bond = this.findClosestBond(pos);
        if (ret == null || ret.dist > 0.4 * this.scale) // hack
            updret('Bond', bond);
    }
    if (!maps || maps.indexOf('chiralFlags') >= 0) {
        var flag = rnd.ReChiralFlag.findClosest(this, pos);
        updret('ChiralFlag', flag); // [MK] TODO: replace this with map name, 'ChiralFlag' -> 'chiralFlags', to avoid the extra mapping "if (ci.type == 'ChiralFlag') ci.map = 'chiralFlags';"
    }
    if (!maps || maps.indexOf('sgroupData') >= 0) {
        var sgd = rnd.ReDataSGroupData.findClosest(this, pos);
        updret('DataSGroupData', sgd);
    }
    if (!maps || maps.indexOf('sgroups') >= 0) {
        var sg = rnd.ReSGroup.findClosest(this, pos);
        updret('SGroup', sg);
    }
    if (!maps || maps.indexOf('rxnArrows') >= 0) {
        var arrow = rnd.ReRxnArrow.findClosest(this, pos);
        updret('RxnArrow',arrow);
    }
    if (!maps || maps.indexOf('rxnPluses') >= 0) {
        var plus = rnd.ReRxnPlus.findClosest(this, pos);
        updret('RxnPlus',plus);
    }
    if (!maps || maps.indexOf('frags') >= 0) {
        var frag = rnd.ReFrag.findClosest(this, pos, skip && skip.map == 'atoms' ? skip.id : undefined);
        updret('Fragment', frag);
    }
    if (!maps || maps.indexOf('rgroups') >= 0) {
        var rgroup = rnd.ReRGroup.findClosest(this, pos);
        updret('RGroup', rgroup);
    }

	ret = ret || {
		'type':'Canvas',
		'id':-1
	};
	return ret;
};

rnd.Render.prototype.addItemPath = function (visel, group, path, rbb)
{
    if (!path) return; // [RB] thats ok for some hidden objects (fragment)
	var bb = rbb ? util.Box2Abs.fromRelBox(rbb) : null;
	var offset = this.offset;
	if (offset != null) {
		if (bb != null)
			bb.translate(offset);
		path.translate(offset.x, offset.y);
	}
	visel.add(path, bb);
	this.ctab.insertInLayer(rnd.ReStruct.layerMap[group], path);
};

rnd.Render.prototype.setZoom = function (zoom) {
	this.zoom = zoom;
	this._setPaperSize(this.sz);
};

rnd.Render.prototype.extendCanvas = function (x0, y0, x1, y1) {
	var ex = 0, ey = 0, dx = 0, dy = 0;
	x0 = x0-0;
	x1 = x1-0;
	y0 = y0-0;
	y1 = y1-0;

	if (x0 < 0) {
		ex += -x0;
		dx += -x0;
	}
	if (y0 < 0) {
		ey += -y0;
		dy += -y0;
	}

	var szx = this.sz.x * this.zoom, szy = this.sz.y * this.zoom;
	if (szx < x1) {
		ex += x1 - szx;
	}
	if (szy < y1) {
		ey += y1 - szy;
	}

	var d = new util.Vec2(dx, dy).scaled(1 / this.zoom);
	if (ey > 0 || ex > 0) {
		var e = new util.Vec2(ex, ey).scaled(1 / this.zoom);
		var sz = this.sz.add(e);

		this.setPaperSize(sz);
		if (d.x > 0 || d.y > 0) {
			this.setOffset(this.offset.add(d));
			this.ctab.translate(d);
			this.bb.translate(d);
		}
	}
	return d;
};

rnd.Render.prototype.setScale = function (z) {
	if (this.offset)
		this.offset = this.offset.scaled(1/z).scaled(this.zoom);
	this.scale = this.baseScale * this.zoom;
	this.settings = null;
	this.update(true);
};

rnd.Render.prototype.setViewBox = function (z) {
	if (!this.useOldZoom)
		this.paper.canvas.setAttribute("viewBox",'0 0 ' + this.sz.x + ' ' + this.sz.y);
	else
		this.setScale(z);
};
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.Prototype)
	throw new Error("Prototype.js should be loaded first");
if (!window.rnd || !rnd.ReStruct)
	throw new Error("rnd.MolData should be defined prior to loading this file");

// TODO re-factoring needed: client_area parameter is excessive, should be available in render
rnd.Editor = function(render)
{
    this.ui = ui; // TODO ui ref should be passed as a parameter
    this.render = render;

    this._selectionHelper = new rnd.Editor.SelectionHelper(this);
};
rnd.Editor.prototype.selectAll = function() {
    var selection = {};
    for (var map in rnd.ReStruct.maps) {
        selection[map] = ui.render.ctab[map].ikeys();
    }
    this._selectionHelper.setSelection(selection);
};
rnd.Editor.prototype.deselectAll = function() {
    this._selectionHelper.setSelection();
};
rnd.Editor.prototype.toolFor = function(tool) {
    if (tool == 'selector_lasso') {
        return new rnd.Editor.LassoTool(this, 0);
    } else if (tool == 'selector_square') {
        return new rnd.Editor.LassoTool(this, 1);
    } else if (tool == 'selector_fragment') {
        return new rnd.Editor.LassoTool(this, 1, true);
    } else if (tool == 'select_erase') {
        return new rnd.Editor.EraserTool(this, 1); // TODO last selector mode is better
    } else if (tool.startsWith('atom_')) {
        return new rnd.Editor.AtomTool(this, ui.atomLabel(tool)); // TODO should not refer ui directly, re-factoring needed
    } else if (tool.startsWith('bond_')) {
        return new rnd.Editor.BondTool(this, ui.bondType(tool)); // TODO should not refer ui directly, re-factoring needed
    } else if (tool == 'chain') {
        return new rnd.Editor.ChainTool(this);
    } else if (tool.startsWith('template_')) {
        return new rnd.Editor.TemplateTool(this, parseInt(tool.split('_')[1]));
    } else if (tool == 'charge_plus') {
        return new rnd.Editor.ChargeTool(this, 1);
    } else if (tool == 'charge_minus') {
        return new rnd.Editor.ChargeTool(this, -1);
    } else if (tool == 'sgroup') {
        return new rnd.Editor.SGroupTool(this);
    } else if (tool == 'paste') {
        return new rnd.Editor.PasteTool(this);
    } else if (tool == 'reaction_arrow') {
        return new rnd.Editor.ReactionArrowTool(this);
    } else if (tool == 'reaction_plus') {
        return new rnd.Editor.ReactionPlusTool(this);
    } else if (tool == 'reaction_map') {
        return new rnd.Editor.ReactionMapTool(this);
    } else if (tool == 'reaction_unmap') {
        return new rnd.Editor.ReactionUnmapTool(this);
    } else if (tool == 'rgroup_label') {
        return new rnd.Editor.RGroupAtomTool(this);
    } else if (tool == 'rgroup_fragment') {
        return new rnd.Editor.RGroupFragmentTool(this);
    } else if (tool == 'rgroup_attpoints') {
        return new rnd.Editor.APointTool(this);
    }
    return null;
};


rnd.Editor.SelectionHelper = function(editor) {
    this.editor = editor;
};
rnd.Editor.SelectionHelper.prototype.setSelection = function(selection, add) {
    if (!('selection' in this) || !add) {
        this.selection = {};
        for (var map1 in rnd.ReStruct.maps) this.selection[map1] = []; // TODO it should NOT be mandatory
    }
    if (selection && 'id' in selection && 'map' in selection) {
        (selection[selection.map] = selection[selection.map] || []).push(selection.id);
    }
    if (selection) {
        for (var map2 in this.selection) {
            if (map2 in selection) {
                for (var i = 0; i < selection[map2].length; i++) {
                    if (this.selection[map2].indexOf(selection[map2][i]) < 0) {
                        this.selection[map2].push(selection[map2][i]);
                    }
                }
            }
        }
    }
    // "auto-select" the atoms for the bonds in selection
    if (!Object.isUndefined(this.selection.bonds)) {
        this.selection.bonds.each(
            function(bid) {
                var bond = this.editor.render.ctab.molecule.bonds.get(bid);
                selection.atoms = selection.atoms || [];
                if (this.selection.atoms.indexOf(bond.begin) < 0) {
                    this.selection.atoms.push(bond.begin);
                }
                if (this.selection.atoms.indexOf(bond.end) < 0) {
                    this.selection.atoms.push(bond.end);
                }
            },
            this
        );
    }
    // "auto-select" the bonds with both atoms selected
    if ('atoms' in this.selection) {
        this.editor.render.ctab.molecule.bonds.each(
            function(bid) {
                if (!('bonds' in this.selection) || this.selection.bonds.indexOf(bid) < 0) {
                    var bond = this.editor.render.ctab.molecule.bonds.get(bid);
                    if (this.selection.atoms.indexOf(bond.begin) >= 0 && this.selection.atoms.indexOf(bond.end) >= 0) {
                        this.selection.bonds = this.selection.bonds || [];
                        this.selection.bonds.push(bid);
                    }
                }
            },
            this
        );
    }
    this.editor.render.setSelection(this.selection);
    this.editor.render.update();

    ui.updateSelection(this.selection, true); // TODO to be removed (used temporary until no new Undo/Redo tools implemented)
    ui.updateClipboardButtons(); // TODO notify ui about selection
};
rnd.Editor.SelectionHelper.prototype.isSelected = function(item) {
    return 'selection' in this
        && !Object.isUndefined(this.selection[item.map])
        && this.selection[item.map].indexOf(item.id) > -1;
};


rnd.Editor.EditorTool = function(editor) {
    this.editor = editor;
};
rnd.Editor.EditorTool.prototype.processEvent = function(name, event) {
    if (!('touches' in event) || event.touches.length == 1) {
        if (name + '0' in this) 
            return this[name + '0'](event); 
        else if (name in this) 
            return this[name](event);
        console.log('EditorTool.dispatchEvent: event \'' + name + '\' is not handled.');
    } else if ('lastEvent' in this.OnMouseDown0) {
        // here we finish previous MouseDown and MouseMoves with simulated MouseUp
        // before gesture (canvas zoom, scroll, rotate) started
        return this.OnMouseUp0(event);
    }
};
rnd.Editor.EditorTool.prototype.OnMouseOver = function() {};
rnd.Editor.EditorTool.prototype.OnMouseDown = function() {};
rnd.Editor.EditorTool.prototype.OnMouseMove = function() {};
rnd.Editor.EditorTool.prototype.OnMouseUp = function() {};
rnd.Editor.EditorTool.prototype.OnClick = function() {};
rnd.Editor.EditorTool.prototype.OnDblClick = function() {};
rnd.Editor.EditorTool.prototype.OnMouseOut = function() {};
rnd.Editor.EditorTool.prototype.OnKeyPress = function() {};
rnd.Editor.EditorTool.prototype.OnCancel = function() {}; // called when we abandon the tool
rnd.Editor.EditorTool.prototype.OnMouseDown0 = function(event) {
    if (this.editor.ui.hideBlurredControls()) return true; // TODO review

    this.OnMouseDown0.lastEvent = event;
    this.OnMouseMove0.lastEvent = event;

    if ('OnMouseDown' in this) return this.OnMouseDown(event);
};
rnd.Editor.EditorTool.prototype.OnMouseMove0 = function(event) {
    this.OnMouseMove0.lastEvent = event;

    if ('OnMouseMove' in this) return this.OnMouseMove(event);
};
rnd.Editor.EditorTool.prototype.OnMouseUp0 = function(event) {
    // here we suppress event we got when second touch released in guesture
    if (!('lastEvent' in this.OnMouseDown0)) return true;

    if ('lastEvent' in this.OnMouseMove0) {
        // this data is missing for 'touchend' event when last finger is out
        event = Object.clone(event); // pageX & pageY properties are readonly in Opera
        event.pageX = this.OnMouseMove0.lastEvent.pageX;
        event.pageY = this.OnMouseMove0.lastEvent.pageY;
    }

    try {
        if ('OnMouseUp' in this) return this.OnMouseUp(event);
    } finally {
        delete this.OnMouseDown0.lastEvent;
    }
};
rnd.Editor.EditorTool.prototype.OnKeyPress0 = function(event) {
    if (!((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx)) && !event.altKey && ('lastEvent' in this.OnMouseMove0)) {
        if (114 == (Prototype.Browser.IE ? event.keyCode : event.which)) { // 'r'
            return rnd.Editor.RGroupAtomTool.prototype.OnMouseUp.call(this, this.OnMouseMove0.lastEvent);
        }
        var ci = this.editor.render.findItem(this.OnMouseMove0.lastEvent);
        if (ci) {
            var labels = {
                Br : 66, Cl : 67, A: 97, C: 99, F : 102, H : 104, I : 105, N : 110, O : 111, P : 112, S : 115
            };
            for (var label in labels) {
                if (labels[label] == (Prototype.Browser.IE ? event.keyCode : event.which)) {
                    ci.label = { label : label };
                    if (ci.map == 'atoms') {
                        this.editor.ui.addUndoAction(ui.Action.fromAtomAttrs(ci.id, ci.label));
                    } else if (ci.id == -1) {
                        this.editor.ui.addUndoAction(
                            this.editor.ui.Action.fromAtomAddition(
                                this.editor.ui.page2obj(this.OnMouseMove0.lastEvent),
                                ci.label
                            ),
                            true
                        );
                    }
                    this.editor.ui.render.update();
                    return true;
                }
            }
        }
    }
    if ('OnKeyPress' in this) return this.OnKeyPress(event);
    return false;
};
rnd.Editor.EditorTool.prototype._calcAngle = function (pos0, pos1) {
    var v = util.Vec2.diff(pos1, pos0);
    var angle = Math.atan2(v.y, v.x);
    var sign = angle < 0 ? - 1 : 1;
    var floor = Math.floor(Math.abs(angle) / (Math.PI / 12)) * (Math.PI / 12);
    angle = sign * (floor + ((Math.abs(angle) - floor < Math.PI / 24) ? 0 : Math.PI / 12));
    return angle;
};
rnd.Editor.EditorTool.prototype._calcNewAtomPos = function(pos0, pos1) {
    var v = new util.Vec2(1, 0).rotate(this._calcAngle(pos0, pos1));
    v.add_(pos0);
    return v;
};


rnd.Editor.EditorTool.HoverHelper = function(editorTool) {
    this.editorTool = editorTool;
};
rnd.Editor.EditorTool.HoverHelper.prototype.hover = function(ci) {
    if (ci && ci.type == 'Canvas')
        ci = null;
    // TODO add custom highlight style parameter, to be used when fusing atoms, sgroup children highlighting, etc
    if ('ci' in this && (!ci || this.ci.type != ci.type || this.ci.id != ci.id)) {
        this.editorTool.editor.render.highlightObject(this.ci, false);
        delete this.ci;
    }
    if (ci && this.editorTool.editor.render.highlightObject(ci, true)) {
        this.ci = ci;
    }
};

rnd.Editor.LassoTool = function(editor, mode, fragment) {
    this.editor = editor;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
    this._lassoHelper = new rnd.Editor.LassoTool.LassoHelper(mode || 0, editor, fragment);
    this._sGroupHelper = new rnd.Editor.SGroupTool.SGroupHelper(editor);
};
rnd.Editor.LassoTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.LassoTool.prototype.OnMouseDown = function(event) {
    var render = this.editor.render;
    var ctab = render.ctab;
    this._hoverHelper.hover(null); // TODO review hovering for touch devices
    var selectFragment = (this._lassoHelper.fragment || event.ctrlKey);
    var ci = this.editor.render.findItem(event, selectFragment ? ['frags', 'rxnArrows', 'rxnPluses', 'chiralFlags'] : ['atoms', 'bonds', 'sgroups', 'sgroupData', 'rxnArrows', 'rxnPluses', 'chiralFlags']);
    if (!ci || ci.type == 'Canvas') {
        if (!this._lassoHelper.fragment)
            this._lassoHelper.begin(event);
    } else {
        this._hoverHelper.hover(null);
        if ('onShowLoupe' in this.editor.render)
            this.editor.render.onShowLoupe(true);
        if (ci.map == 'frags') {
            var frag = ctab.frags.get(ci.id);
            var atoms = frag.fragGetAtoms(render, ci.id);
            this.editor._selectionHelper.setSelection({'atoms':atoms}, event.shiftKey);
        } else if (!this.editor._selectionHelper.isSelected(ci)) {
            this.editor._selectionHelper.setSelection(ci, event.shiftKey);
        }
        this.dragCtx = {
            item : ci,
            xy0 : this.editor.ui.page2obj(event)
        };
        if (ci.map == 'atoms') {
            var self = this;
            this.dragCtx.timeout = setTimeout(
                function() {
                    delete self.dragCtx;
                    self.editor._selectionHelper.setSelection(null);
                    self.editor.ui.showLabelEditor(ci.id);
                },
                750
            );
            this.dragCtx.stopTapping = function() {
                if ('timeout' in self.dragCtx) {
                    clearTimeout(self.dragCtx.timeout);
                    delete self.dragCtx.timeout;
                }
            }
        }
    }
    return true;
};
rnd.Editor.LassoTool.prototype.OnMouseMove = function(event) {
    if ('dragCtx' in this) {
        if ('stopTapping' in this.dragCtx) this.dragCtx.stopTapping();
        // moving selected objects
        if (this.dragCtx.action)
            this.dragCtx.action.perform();
        this.dragCtx.action = ui.Action.fromMultipleMove(
            this.editor._selectionHelper.selection,
            this.editor.ui.page2obj(event).sub(this.dragCtx.xy0));
        // finding & highlighting object to stick to
        if (['atoms'/*, 'bonds'*/].indexOf(this.dragCtx.item.map) >= 0) {
            // TODO add bond-to-bond fusing
            var ci = this.editor.render.findItem(event, [this.dragCtx.item.map], this.dragCtx.item);
            this._hoverHelper.hover(ci.map == this.dragCtx.item.map ? ci : null);
        }
        this.editor.render.update();
    } else if (this._lassoHelper.running()) {
        this.editor._selectionHelper.setSelection(this._lassoHelper.addPoint(event), event.shiftKey);
    } else {
        this._hoverHelper.hover(
            this.editor.render.findItem(event, (this._lassoHelper.fragment || event.ctrlKey) ? ['frags', 'rxnArrows', 'rxnPluses', 'chiralFlags'] : ['atoms', 'bonds', 'sgroups', 'sgroupData', 'rxnArrows', 'rxnPluses', 'chiralFlags'])
        );
    }
    return true;
};
rnd.Editor.LassoTool.prototype.OnMouseUp = function(event) {
    if ('dragCtx' in this) {
        if ('stopTapping' in this.dragCtx) this.dragCtx.stopTapping();
        if (['atoms'/*, 'bonds'*/].indexOf(this.dragCtx.item.map) >= 0) {
            // TODO add bond-to-bond fusing
            var ci = this.editor.render.findItem(event, [this.dragCtx.item.map], this.dragCtx.item);
            if (ci.map == this.dragCtx.item.map) {
                this._hoverHelper.hover(null);
                this.editor._selectionHelper.setSelection();
                this.dragCtx.action = this.dragCtx.action
                    ? this.editor.ui.Action.fromAtomMerge(this.dragCtx.item.id, ci.id).mergeWith(this.dragCtx.action)
                    : this.editor.ui.Action.fromAtomMerge(this.dragCtx.item.id, ci.id);
            }
        }
        this.editor.ui.addUndoAction(this.dragCtx.action, true);
        this.editor.render.update();
        delete this.dragCtx;
    } else {
        if (this._lassoHelper.running()) { // TODO it catches more events than needed, to be re-factored
            this.editor._selectionHelper.setSelection(this._lassoHelper.end(event), event.shiftKey);
        } else if (this._lassoHelper.fragment) {
            this.editor._selectionHelper.setSelection();
        }
    }
    return true;
};
rnd.Editor.LassoTool.prototype.OnDblClick = function(event) {
    var ci = this.editor.render.findItem(event);
    if (ci.map == 'atoms') {
        this.editor.ui.showAtomProperties(ci.id);
    } else if (ci.map == 'bonds') {
        this.editor.ui.showBondProperties(ci.id);
    } else if (ci.map == 'sgroups') {
        this._sGroupHelper.showPropertiesDialog(ci.id);
//    } else if (ci.map == 'sgroupData') {
//        this._sGroupHelper.showPropertiesDialog(ci.sgid);
    }
    return true;
};
rnd.Editor.LassoTool.prototype.OnCancel = function() {
    if ('dragCtx' in this) {
        if ('stopTapping' in this.dragCtx) this.dragCtx.stopTapping();
        this.editor.ui.addUndoAction(this.dragCtx.action, true);
        this.editor.render.update();
        delete this.dragCtx;
    }
    this.editor._selectionHelper.setSelection();
};


rnd.Editor.LassoTool.LassoHelper = function(mode, editor, fragment) {
    this.mode = mode;
    this.fragment = fragment;
    this.editor = editor;
};
rnd.Editor.LassoTool.LassoHelper.prototype.getSelection = function() {
    if (this.mode == 0) {
        return this.editor.ui.render.getElementsInPolygon(this.points);
    } else if (this.mode == 1) {
        return this.editor.ui.render.getElementsInRectangle(this.points[0], this.points[1]);
    } else {
        throw new Error("Selector mode unknown");
    }
};
rnd.Editor.LassoTool.LassoHelper.prototype.begin = function(event) {
    this.points = [ this.editor.ui.page2obj(event) ];
    if (this.mode == 1) {
        this.points.push(this.points[0]);
    }
};
rnd.Editor.LassoTool.LassoHelper.prototype.running = function() {
    return 'points' in this;
};
rnd.Editor.LassoTool.LassoHelper.prototype.addPoint = function(event) {
    if (!this.running()) return false;
    if (this.mode == 0) {
        this.points.push(this.editor.ui.page2obj(event));
        this.editor.render.drawSelectionPolygon(this.points);
    } else if (this.mode == 1) {
        this.points = [ this.points[0], this.editor.ui.page2obj(event) ];
        this.editor.render.drawSelectionRectangle(this.points[0], this.points[1]);
    }
    return this.getSelection();
};
rnd.Editor.LassoTool.LassoHelper.prototype.end = function() {
    var ret = this.getSelection();
    if ('points' in this) {
        this.editor.render.drawSelectionPolygon(null);
        delete this.points;
    }
    return ret;
};


rnd.Editor.EraserTool = function(editor, mode) {
    this.editor = editor;

    this.maps = ['atoms', 'bonds', 'rxnArrows', 'rxnPluses', 'rgroups', 'sgroups', 'sgroupData', 'chiralFlags'];
    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
    this._lassoHelper = new rnd.Editor.LassoTool.LassoHelper(mode || 0, editor);
};
rnd.Editor.EraserTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.EraserTool.prototype.OnMouseDown = function(event) {
    var ci = this.editor.render.findItem(event, this.maps);
    if (!ci || ci.type == 'Canvas') {
        this._lassoHelper.begin(event);
    }
};
rnd.Editor.EraserTool.prototype.OnMouseMove = function(event) {
    if (this._lassoHelper.running()) {
        this.editor._selectionHelper.setSelection(
            this._lassoHelper.addPoint(event)
            // TODO add "no-auto-atoms-selection" option (see selection left on canvas after erasing)
        );
    } else {
        this._hoverHelper.hover(this.editor.render.findItem(event, this.maps));
    }
};
rnd.Editor.EraserTool.prototype.OnMouseUp = function(event) {
    if (this._lassoHelper.running()) { // TODO it catches more events than needed, to be re-factored
        this.editor.ui.addUndoAction(this.editor.ui.Action.fromFragmentDeletion(this._lassoHelper.end(event)));
        for (var map1 in rnd.ReStruct.maps) ui.selection[map1] = []; // TODO to be deleted when ui.selection eliminated
        this.editor.ui.render.update();
        this.editor.ui.updateClipboardButtons(); // TODO review
    } else {
        var ci = this.editor.render.findItem(event, this.maps);
        if (ci && ci.type != 'Canvas') {
            this._hoverHelper.hover(null);
            if (ci.map == 'atoms') {
                this.editor.ui.addUndoAction(this.editor.ui.Action.fromAtomDeletion(ci.id));
            } else if (ci.map == 'bonds') {
                this.editor.ui.addUndoAction(this.editor.ui.Action.fromBondDeletion(ci.id));
            } else if (ci.map == 'sgroups' || ci.map == 'sgroupData') {
                this.editor.ui.addUndoAction(this.editor.ui.Action.fromSgroupDeletion(ci.id));
            } else if (ci.map == 'rxnArrows') {
                this.editor.ui.addUndoAction(this.editor.ui.Action.fromArrowDeletion(ci.id));
            } else if (ci.map == 'rxnPluses') {
                this.editor.ui.addUndoAction(this.editor.ui.Action.fromPlusDeletion(ci.id));
            } else if (ci.map == 'chiralFlags') {
                this.editor.ui.addUndoAction(this.editor.ui.Action.fromChiralFlagDeletion());
            } else {
                // TODO re-factoring needed - should be "map-independent"
                console.log('EraserTool: unable to delete the object ' + ci.map + '[' + ci.id + ']');
                return;
            }
            for (var map2 in rnd.ReStruct.maps) ui.selection[map2] = []; // TODO to be deleted when ui.selection eliminated
            this.editor.ui.render.update();
            this.editor.ui.updateClipboardButtons(); // TODO review
            this.editor._selectionHelper.setSelection();
        }
    }
};


rnd.Editor.AtomTool = function(editor, atomProps) {
    this.editor = editor;
    this.atomProps = atomProps;
    this.bondProps = { type : 1, stereo : chem.Struct.BOND.STEREO.NONE };

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.AtomTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.AtomTool.prototype.OnMouseDown = function(event) {
    this._hoverHelper.hover(null);
    var ci = this.editor.render.findItem(event, ['atoms']);
    if (!ci || ci.type == 'Canvas') {
        this.dragCtx = {
            xy0 : this.editor.ui.page2obj(event)
        };
    } else if (ci.map == 'atoms') {
        this.dragCtx = {
            item : ci,
            xy0 : this.editor.ui.page2obj(event)
        };
    }
};
rnd.Editor.AtomTool.prototype.OnMouseMove = function(event) {
    var _E_ = this.editor, _R_ = _E_.render;
    if ('dragCtx' in this && 'item' in this.dragCtx) {
        var _DC_ = this.dragCtx;
        var newAtomPos = this._calcNewAtomPos(
            _R_.atomGetPos(_DC_.item.id), _E_.ui.page2obj(event)
        );
        if ('action' in _DC_) {
            _DC_.action.perform();
        }
        // TODO [RB] kludge fix for KETCHER-560. need to review
        //BEGIN
        /*
        var action_ret = _E_.ui.Action.fromBondAddition(
            this.bondProps, _DC_.item.id, this.atomProps, newAtomPos, newAtomPos
        );
        */
        var action_ret = _E_.ui.Action.fromBondAddition(
            this.bondProps, _DC_.item.id, Object.clone(this.atomProps), newAtomPos, newAtomPos
        );
        //END
        _DC_.action = action_ret[0];
        _DC_.aid2 = action_ret[2];
        _R_.update();
    } else {
        this._hoverHelper.hover(_R_.findItem(event, ['atoms']));
    }
};
rnd.Editor.AtomTool.prototype.OnMouseUp = function(event) {
    if ('dragCtx' in this) {
        var _UI_ = this.editor.ui, _DC_ = this.dragCtx;
        _UI_.addUndoAction(
            'action' in _DC_
                ? _DC_.action
                : 'item' in _DC_
                    ? _UI_.Action.fromAtomAttrs(_DC_.item.id, this.atomProps)
                    : _UI_.Action.fromAtomAddition(_UI_.page2obj(event), this.atomProps),
            true
        );
        this.editor.render.update();
        delete this.dragCtx;
    }
};


rnd.Editor.BondTool = function(editor, bondProps) {
    this.editor = editor;
    this.atomProps = { label : 'C' };
    this.bondProps = bondProps;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.BondTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.BondTool.prototype.OnMouseDown = function(event) {
    this._hoverHelper.hover(null);
    this.dragCtx = {
        xy0 : this.editor.ui.page2obj(event),
        item : this.editor.render.findItem(event, ['atoms', 'bonds'])
    };
    if (!this.dragCtx.item || this.dragCtx.item.type == 'Canvas') delete this.dragCtx.item;
    return true;
};
rnd.Editor.BondTool.prototype.OnMouseMove = function(event) {
    var _E_ = this.editor, _R_ = _E_.render;
    if ('dragCtx' in this) {
        var _DC_ = this.dragCtx;
        if (!('item' in _DC_) || _DC_.item.map == 'atoms') {
            if ('action' in _DC_) _DC_.action.perform();
            var i1, i2, p1, p2;
            if (('item' in _DC_ && _DC_.item.map == 'atoms')) {
                i1 = _DC_.item.id;
                i2 = _R_.findItem(event, ['atoms'], _DC_.item);
            } else {
                i1 = this.atomProps;
                p1 = _DC_.xy0;
                i2 = _R_.findItem(event, ['atoms']);
            }
            if (i2 && i2.map == 'atoms') {
                i2 = i2.id;
            } else {
                i2 = this.atomProps;
                if (p1) {
                    p2 = this._calcNewAtomPos(p1, _E_.ui.page2obj(event))
                } else {
                    p1 = this._calcNewAtomPos(_R_.atomGetPos(i1), _E_.ui.page2obj(event));
                }
            }
            _DC_.action = _E_.ui.Action.fromBondAddition(this.bondProps, i1, i2, p1, p2)[0];
            _R_.update();
            return true;
        }
    }
    this._hoverHelper.hover(_R_.findItem(event, ['atoms', 'bonds']));
    return true;
};
rnd.Editor.BondTool.prototype.OnMouseUp = function(event) {
    if ('dragCtx' in this) {
        var _UI_ = this.editor.ui, _DC_ = this.dragCtx;
        if ('action' in _DC_) {
            _UI_.addUndoAction(_DC_.action);
        } else if (!('item' in _DC_)) {
            var xy = this.editor.ui.page2obj(event);
            var v = new util.Vec2(1.0 / 2, 0).rotate(
                this.bondProps.type == chem.Struct.BOND.TYPE.SINGLE ? -Math.PI / 6 : 0
            );
            _UI_.addUndoAction(
                _UI_.Action.fromBondAddition(
                    this.bondProps,
                    { label : 'C' },
                    { label : 'C' },
                    { x : xy.x - v.x, y : xy.y - v.y},
                    { x : xy.x + v.x, y : xy.y + v.y}
                )[0]
            );
        } else if (_DC_.item.map == 'atoms') {
            var atom = _UI_.atomForNewBond(_DC_.item.id);
            _UI_.addUndoAction(
                _UI_.Action.fromBondAddition(this.bondProps, _DC_.item.id, atom.atom, atom.pos)[0]
            );
        } else if (_DC_.item.map == 'bonds') {
            var bondProps = Object.clone(this.bondProps);
            var bond = _UI_.ctab.bonds.get(_DC_.item.id);

            if (bondProps.stereo != chem.Struct.BOND.STEREO.NONE
                && bond.type == chem.Struct.BOND.TYPE.SINGLE
                && bondProps.type == chem.Struct.BOND.TYPE.SINGLE
                && bond.stereo == bondProps.stereo)
            {
                _UI_.addUndoAction(_UI_.Action.fromBondFlipping(_DC_.item.id));
            } else {
                if (bond.type == bondProps.type) {
                    if (bond.type == chem.Struct.BOND.TYPE.SINGLE) {
                        if (bond.stereo == chem.Struct.BOND.STEREO.NONE && bond.stereo == bondProps.stereo) {
                            bondProps.type = chem.Struct.BOND.TYPE.DOUBLE;
                        }
                    } else if (bond.type == chem.Struct.BOND.TYPE.DOUBLE) {
                        bondProps.type = chem.Struct.BOND.TYPE.TRIPLE;
                    } else if (bond.type == chem.Struct.BOND.TYPE.TRIPLE) {
                        bondProps.type = chem.Struct.BOND.TYPE.SINGLE;
                    }
                }
                _UI_.addUndoAction(
                    _UI_.Action.fromBondAttrs(_DC_.item.id, bondProps, _UI_.bondFlipRequired(bond, bondProps)),
                    true
                );
            }
        }
        this.editor.render.update();
        delete this.dragCtx;
    }
    return true;
};


rnd.Editor.ChainTool = function(editor) {
    this.editor = editor;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.ChainTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.ChainTool.prototype.OnMouseDown = function(event) {
    this._hoverHelper.hover(null);
    this.dragCtx = {
        xy0 : this.editor.ui.page2obj(event),
        item : this.editor.render.findItem(event, ['atoms'])
    };
    if (!this.dragCtx.item || this.dragCtx.item.type == 'Canvas') delete this.dragCtx.item;
    return true;
};
rnd.Editor.ChainTool.prototype.OnMouseMove = function(event) {
    var _E_ = this.editor, _R_ = _E_.render;
    if ('dragCtx' in this) {
        var _DC_ = this.dragCtx;
        if ('action' in _DC_) _DC_.action.perform();
        var pos0 = 'item' in _DC_ ? _R_.atomGetPos(_DC_.item.id) : _DC_.xy0;
        var pos1 = _E_.ui.page2obj(event);
        _DC_.action = _E_.ui.Action.fromChain(
            pos0,
            this._calcAngle(pos0, pos1),
            Math.ceil(util.Vec2.diff(pos1, pos0).length()),
            'item' in _DC_ ? _DC_.item.id : null
        );
        _R_.update();
        return true;
    }
    this._hoverHelper.hover(_R_.findItem(event, ['atoms']));
    return true;
};
rnd.Editor.ChainTool.prototype.OnMouseUp = function() {
    if ('dragCtx' in this) {
        if ('action' in this.dragCtx) {
            this.editor.ui.addUndoAction(this.dragCtx.action);
        }
        delete this.dragCtx;
    }
    return true;
};


rnd.Editor.TemplateTool = function(editor, template) {
    this.editor = editor;
    this.template = template;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.TemplateTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.TemplateTool.prototype.templates = [
    [1, 2, 1, 2, 1, 2],
    [1, 2, 1, 2, 1],
    [1, 1, 1, 1, 1, 1],
    [1, 1, 1, 1, 1],
    [1, 1, 1],
    [1, 1, 1, 1],
    [1, 1, 1, 1, 1, 1, 1],
    [1, 1, 1, 1, 1, 1, 1, 1]
];
// TODO implement rotation around fusing atom / flipping over fusing bond
rnd.Editor.TemplateTool.prototype.OnMouseMove = function(event) {
    this._hoverHelper.hover(this.editor.render.findItem(event, ['atoms', 'bonds']));
};
rnd.Editor.TemplateTool.prototype.OnMouseUp = function(event) {
    this._hoverHelper.hover(null);
    var ci = this.editor.render.findItem(event, ['atoms', 'bonds']);
    if (!ci || ci.type == 'Canvas') {
        this.editor.ui.addUndoAction(
            this.editor.ui.Action.fromPatternOnCanvas(this.editor.ui.page2obj(event), this.templates[this.template]),
            true
        );
        this.editor.ui.render.update();
    } else if (ci.map == 'atoms') {
        this.editor.ui.addUndoAction(
            this.editor.ui.Action.fromPatternOnAtom(ci.id, this.templates[this.template]),
            true
        );
        this.editor.ui.render.update();
    } else if (ci.map == 'bonds') {
        this.editor.ui.addUndoAction(
            this.editor.ui.Action.fromPatternOnElement(ci.id, this.templates[this.template], false),
            true
        );
        this.editor.ui.render.update();
    }
};


rnd.Editor.ChargeTool = function(editor, charge) { // TODO [RB] should be "pluggable"
    this.editor = editor;
    this.charge = charge;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.ChargeTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.ChargeTool.prototype.OnMouseMove = function(event) {
    this._hoverHelper.hover(this.editor.render.findItem(event, ['atoms']));
    return true;
};
rnd.Editor.ChargeTool.prototype.OnMouseUp = function(event) {
    var _E_ = this.editor, _R_ = _E_.render;
    var ci = _R_.findItem(event, ['atoms']);
    if (ci && ci.map == 'atoms') {
        this._hoverHelper.hover(null);
        _E_.ui.addUndoAction(
            _E_.ui.Action.fromAtomAttrs(ci.id, { charge : _R_.ctab.molecule.atoms.get(ci.id).charge + this.charge })
        );
        _R_.update();
    }
    return true;
};


rnd.Editor.RGroupAtomTool = function(editor) {
    this.editor = editor;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.RGroupAtomTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.RGroupAtomTool.prototype.OnMouseMove = function(event) {
    this._hoverHelper.hover(this.editor.render.findItem(event, ['atoms']));
};
rnd.Editor.RGroupAtomTool.prototype.OnMouseUp = function(event) {
    var ci = this.editor.render.findItem(event, ['atoms']);
    if (!ci || ci.type == 'Canvas') {
        this._hoverHelper.hover(null);
        this.editor.ui.showRGroupTable({
            onOk : function(rgNew) {
                if (rgNew) {
                    this.editor.ui.addUndoAction(
                        this.editor.ui.Action.fromAtomAddition(
                            this.editor.ui.page2obj(this.OnMouseMove0.lastEvent),
                            { label : 'R#', rglabel : rgNew}
                        ),
                        true
                    );
                    this.editor.ui.render.update();
                }
            }.bind(this)
        });
        return true;
    } else if (ci && ci.map == 'atoms') {
        this._hoverHelper.hover(null);
        var lbOld = this.editor.render.ctab.molecule.atoms.get(ci.id).label;
        var rgOld = this.editor.render.ctab.molecule.atoms.get(ci.id).rglabel;
        this.editor.ui.showRGroupTable({
            selection : rgOld,
            onOk : function(rgNew) {
                if (rgOld != rgNew || lbOld != 'R#') {
                    var newProps = Object.clone(chem.Struct.Atom.attrlist); // TODO review: using Atom.attrlist as a source of default property values
                    if (rgNew) {
                        newProps.label = 'R#';
                        newProps.rglabel = rgNew;
                    } else {
                        newProps.label = 'C';
                    }
                    this.editor.ui.addUndoAction(this.editor.ui.Action.fromAtomAttrs(ci.id, newProps), true);
                    this.editor.ui.render.update();
                }
            }.bind(this)
        });
        return true;
    }
};


rnd.Editor.RGroupFragmentTool = function(editor) {
    this.editor = editor;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.RGroupFragmentTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.RGroupFragmentTool.prototype.OnMouseMove = function(event) {
    this._hoverHelper.hover(this.editor.render.findItem(event, ['frags', 'rgroups']));
};
rnd.Editor.RGroupFragmentTool.prototype.OnMouseUp = function(event) {
    var ci = this.editor.render.findItem(event, ['frags', 'rgroups']);
    if (ci && ci.map == 'frags') {
        this._hoverHelper.hover(null);
        var rgOld = chem.Struct.RGroup.findRGroupByFragment(this.editor.render.ctab.molecule.rgroups, ci.id);
        this.editor.ui.showRGroupTable({
            mode : 'single',
            selection : rgOld ? 1 << (rgOld - 1) : 0,
            onOk : function(rgNew) {
                for (var i = 0; i < 32; i++) 
                    if (rgNew & (1 << i)) { 
                        rgNew = i + 1; 
                        break; 
                    }
                if (rgOld != rgNew) {
                    this.editor.ui.addUndoAction(
                        this.editor.ui.Action.fromRGroupFragment(rgNew, ci.id),
                        true
                    );
                    this.editor.ui.render.update();
                }
            }.bind(this)
        });
        return true;
    }
    else if (ci && ci.map == 'rgroups') {
        this._hoverHelper.hover(null);
        var rg = this.editor.render.ctab.molecule.rgroups.get(ci.id);
        var rgmask = 0; this.editor.render.ctab.molecule.rgroups.each(function(rgid) { rgmask |= (1 << (rgid - 1)); });
        var oldLogic = {
            occurrence : rg.range,
            resth : rg.resth,
            ifthen : rg.ifthen
        };
        this.editor.ui.showRLogicTable({
            rgid : ci.id,
            rlogic : oldLogic,
            rgmask : rgmask,
            onOk : function(newLogic) {
                var props = {};
                if (oldLogic.occurrence != newLogic.occurrence) props.range = newLogic.occurrence;
                if (oldLogic.resth != newLogic.resth) props.resth = newLogic.resth;
                if (oldLogic.ifthen != newLogic.ifthen) props.ifthen = newLogic.ifthen;
                if ('range' in props || 'resth' in props || 'ifthen' in props) {
                    this.editor.ui.addUndoAction(this.editor.ui.Action.fromRGroupAttrs(ci.id, props));
                    this.editor.render.update();
                }
            }.bind(this)
        });
        return true;
    }
};

rnd.Editor.APointTool = function(editor) {
    this.editor = editor;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.APointTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.APointTool.prototype.OnMouseMove = function(event) {
    this._hoverHelper.hover(this.editor.render.findItem(event, ['atoms']));
};
rnd.Editor.APointTool.prototype.OnMouseUp = function(event) {
    var ci = this.editor.render.findItem(event, ['atoms']);
    if (ci && ci.map == 'atoms') {
        this._hoverHelper.hover(null);
        var apOld = this.editor.render.ctab.molecule.atoms.get(ci.id).attpnt;
        this.editor.ui.showAtomAttachmentPoints({
            selection : apOld,
            onOk : function(apNew) {
                if (apOld != apNew) {
                    this.editor.ui.addUndoAction(this.editor.ui.Action.fromAtomAttrs(ci.id, { attpnt : apNew }), true);
                    this.editor.ui.render.update();
                }
            }.bind(this)
        });
        return true;
    }
};


rnd.Editor.ReactionArrowTool = function(editor) {
    this.editor = editor;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.ReactionArrowTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.ReactionArrowTool.prototype.OnMouseDown = function(event) {
    var ci = this.editor.render.findItem(event, ['rxnArrows']);
    if (ci && ci.map == 'rxnArrows') {
        this._hoverHelper.hover(null);
        this.editor._selectionHelper.setSelection(ci);
        this.dragCtx = {
            xy0 : this.editor.ui.page2obj(event)
        };
    }
};
rnd.Editor.ReactionArrowTool.prototype.OnMouseMove = function(event) {
    if ('dragCtx' in this) {
        if (this.dragCtx.action)
            this.dragCtx.action.perform();
        this.dragCtx.action = ui.Action.fromMultipleMove(
            this.editor._selectionHelper.selection,
            this.editor.ui.page2obj(event).sub(this.dragCtx.xy0)
        );
        this.editor.ui.render.update();
    } else {
        this._hoverHelper.hover(this.editor.render.findItem(event, ['rxnArrows']));
    }
};
rnd.Editor.ReactionArrowTool.prototype.OnMouseUp = function(event) {
    if ('dragCtx' in this) {
        this.editor.ui.addUndoAction(this.dragCtx.action, false); // TODO investigate, subsequent undo/redo fails
        this.editor.render.update();
        delete this.dragCtx;
    } else if (this.editor.render.ctab.molecule.rxnArrows.count() < 1) {
        this.editor.ui.addUndoAction(this.editor.ui.Action.fromArrowAddition(this.editor.ui.page2obj(event)));
        this.editor.render.update();
    }
};


rnd.Editor.ReactionPlusTool = function(editor) {
    this.editor = editor;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
};
rnd.Editor.ReactionPlusTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.ReactionPlusTool.prototype.OnMouseDown = function(event) {
    var ci = this.editor.render.findItem(event, ['rxnPluses']);
    if (ci && ci.map == 'rxnPluses') {
        this._hoverHelper.hover(null);
        this.editor._selectionHelper.setSelection(ci);
        this.dragCtx = {
            xy0 : this.editor.ui.page2obj(event)
        };
    }
};
rnd.Editor.ReactionPlusTool.prototype.OnMouseMove = function(event) {
    if ('dragCtx' in this) {
        if (this.dragCtx.action)
            this.dragCtx.action.perform();
        this.dragCtx.action = ui.Action.fromMultipleMove(
            this.editor._selectionHelper.selection,
            this.editor.ui.page2obj(event).sub(this.dragCtx.xy0)
        );
        this.editor.ui.render.update();
    } else {
        this._hoverHelper.hover(this.editor.render.findItem(event, ['rxnPluses']));
    }
};
rnd.Editor.ReactionPlusTool.prototype.OnMouseUp = function(event) {
    if ('dragCtx' in this) {
        this.editor.ui.addUndoAction(this.dragCtx.action, false); // TODO investigate, subsequent undo/redo fails
        this.editor.render.update();
        delete this.dragCtx;
    } else {
        this.editor.ui.addUndoAction(this.editor.ui.Action.fromPlusAddition(this.editor.ui.page2obj(event)));
        this.editor.render.update();
    }
};


rnd.Editor.ReactionMapTool = function(editor) {
    this.editor = editor;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);

    this.editor._selectionHelper.setSelection(null);

    this.rcs = chem.MolfileSaver.getComponents(this.editor.render.ctab.molecule);
};
rnd.Editor.ReactionMapTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.ReactionMapTool.prototype.OnMouseDown = function(event) {
    var ci = this.editor.render.findItem(event, ['atoms']);
    if (ci && ci.map == 'atoms') {
        this._hoverHelper.hover(null);
        this.dragCtx = {
            item : ci,
            xy0 : this.editor.ui.page2obj(event)
        }
    }
};
rnd.Editor.ReactionMapTool.prototype.OnMouseMove = function(event) {
    var rnd = this.editor.render;
    if ('dragCtx' in this) {
        var ci = rnd.findItem(event, ['atoms'], this.dragCtx.item);
        if (ci && ci.map == 'atoms' && this._isValidMap(this.dragCtx.item.id, ci.id)) {
            this._hoverHelper.hover(ci);
            rnd.drawSelectionLine(rnd.atomGetPos(this.dragCtx.item.id), rnd.atomGetPos(ci.id));
        } else {
            this._hoverHelper.hover(null);
            rnd.drawSelectionLine(rnd.atomGetPos(this.dragCtx.item.id), this.editor.ui.page2obj(event));
        }
    } else {
        this._hoverHelper.hover(rnd.findItem(event, ['atoms']));
    }
};
rnd.Editor.ReactionMapTool.prototype.OnMouseUp = function(event) {
    if ('dragCtx' in this) {
        var rnd = this.editor.render;
        var ci = rnd.findItem(event, ['atoms'], this.dragCtx.item);
        if (ci && ci.map == 'atoms' && this._isValidMap(this.dragCtx.item.id, ci.id)) {
            var action = new this.editor.ui.Action();
            var atoms = rnd.ctab.molecule.atoms;
            var atom1 = atoms.get(this.dragCtx.item.id), atom2 = atoms.get(ci.id);
            var aam1 = atom1.aam, aam2 = atom2.aam;
            if (!aam1 || aam1 != aam2) {
                if (aam1 && aam1 != aam2 || !aam1 && aam2) {
                    atoms.each(
                        function(aid, atom) {
                            if (aid != this.dragCtx.item.id && (aam1 && atom.aam == aam1 || aam2 && atom.aam == aam2)) {
                                action.mergeWith(this.editor.ui.Action.fromAtomAttrs(aid, { aam : 0 }));
                            }
                        },
                        this
                    );
                }
                if (aam1) {
                    action.mergeWith(this.editor.ui.Action.fromAtomAttrs(ci.id, { aam : aam1 }));
                } else {
                    var aam = 0; atoms.each(function(aid, atom) { aam = Math.max(aam, atom.aam || 0); });
                    action.mergeWith(this.editor.ui.Action.fromAtomAttrs(this.dragCtx.item.id, { aam : aam + 1 }));
                    action.mergeWith(this.editor.ui.Action.fromAtomAttrs(ci.id, { aam : aam + 1 }));
                }
                this.editor.ui.addUndoAction(action, true);
                rnd.update();
            }
        }
        rnd.drawSelectionLine(null);
        delete this.dragCtx;
    }
    this._hoverHelper.hover(null);
};
rnd.Editor.ReactionMapTool.prototype._isValidMap = function(aid1, aid2) {
    var t1, t2;
    for (var ri = 0; (!t1 || !t2) && ri < this.rcs.reactants.length; ri++) {
        var ro = util.Set.list(this.rcs.reactants[ri]);
        if (!t1 && ro.indexOf(aid1) >= 0) t1 = 'r';
        if (!t2 && ro.indexOf(aid2) >= 0) t2 = 'r';
    }
    for (var pi = 0; (!t1 || !t2) && pi < this.rcs.products.length; pi++) {
        var po = util.Set.list(this.rcs.products[pi]);
        if (!t1 && po.indexOf(aid1) >= 0) t1 = 'p';
        if (!t2 && po.indexOf(aid2) >= 0) t2 = 'p';
    }
    return t1 && t2 && t1 != t2;
};


rnd.Editor.ReactionUnmapTool = function(editor) {
    this.editor = editor;

    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);

    this.editor._selectionHelper.setSelection(null);
};
rnd.Editor.ReactionUnmapTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.ReactionUnmapTool.prototype.OnMouseMove = function(event) {
    var ci = this.editor.render.findItem(event, ['atoms']);
    if (ci && ci.map == 'atoms') {
        this._hoverHelper.hover(this.editor.render.ctab.molecule.atoms.get(ci.id).aam ? ci : null);
    } else {
        this._hoverHelper.hover(null);
    }
};
rnd.Editor.ReactionUnmapTool.prototype.OnMouseUp = function(event) {
    var ci = this.editor.render.findItem(event, ['atoms']);
    var atoms = this.editor.render.ctab.molecule.atoms;
    if (ci && ci.map == 'atoms' && atoms.get(ci.id).aam) {
        var action = new this.editor.ui.Action();
        var aam = atoms.get(ci.id).aam;
        atoms.each(
            function(aid, atom) {
                if (atom.aam == aam) {
                    action.mergeWith(this.editor.ui.Action.fromAtomAttrs(aid, { aam : 0 }));
                }
            },
            this
        );
        this.editor.ui.addUndoAction(action, true);
        this.editor.render.update();
    }
    this._hoverHelper.hover(null);
};

rnd.Editor.SGroupTool = function(editor) {
    this.editor = editor;

    this.maps = ['atoms', 'bonds', 'sgroups', 'sgroupData'];
    this._hoverHelper = new rnd.Editor.EditorTool.HoverHelper(this);
    this._lassoHelper = new rnd.Editor.LassoTool.LassoHelper(1, editor);
    this._sGroupHelper = new rnd.Editor.SGroupTool.SGroupHelper(editor);

    var selection = this.editor.ui.selection;
    if (selection.atoms && selection.atoms.length > 0) {
        // if the selection contrain atoms, create an s-group out of those
        this._sGroupHelper.showPropertiesDialog(null, selection);
    } else {
        // otherwise, clear selection
        this.editor.ui.updateSelection();
    }
};
rnd.Editor.SGroupTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.SGroupTool.prototype.OnMouseDown = function(event) {
    var ci = this.editor.render.findItem(event, this.maps);
    if (!ci || ci.type == 'Canvas') {
        this._lassoHelper.begin(event);
    }
};
rnd.Editor.SGroupTool.prototype.OnMouseMove = function(event) {
    if (this._lassoHelper.running()) {
        this.editor._selectionHelper.setSelection(
            this._lassoHelper.addPoint(event)
            // TODO add "no-auto-atoms-selection" option (see selection left on canvas after erasing)
        );
    } else {
        this._hoverHelper.hover(this.editor.render.findItem(event, this.maps));
    }
};

rnd.Editor.SGroupTool.SGroupHelper = function(editor) {
    this.editor = editor;
    this.selection = null;
};

rnd.Editor.SGroupTool.SGroupHelper.prototype.showPropertiesDialog = function(id, selection) {
    this.selection = selection;

    var render = this.editor.render;
    // check s-group overlappings
    if (id == null)
    {
        var verified = {};
        var atoms_hash = {};

        selection.atoms.each(function (id)
        {
            atoms_hash[id] = true;
        }, this);

        if (!Object.isUndefined(selection.atoms.detect(function (id)
        {
            var sgroups = render.atomGetSGroups(id);

            return !Object.isUndefined(sgroups.detect(function (sid)
            {
                if (sid in verified)
                    return false;

                var sg_atoms = render.sGroupGetAtoms(sid);

                if (sg_atoms.length < selection.atoms.length)
                {
                    if (!Object.isUndefined(sg_atoms.detect(function (aid)
                    {
                        return !(aid in atoms_hash);
                    }, this)))
                    {
                        return true;
                    }
                } else if (!Object.isUndefined(selection.atoms.detect(function (aid)
                {
                    return (sg_atoms.indexOf(aid) == -1);
                }, this)))
                {
                    return true;
                }

                return false;
            }, this));
        }, this)))
        {
            alert("Partial S-group overlapping is not allowed.");
            return;
        }
    }

    this.editor.ui.showSGroupProperties(id, this, this.selection, this.OnPropertiesDialogOk, this.OnPropertiesDialogCancel);
};

rnd.Editor.SGroupTool.prototype.OnMouseUp = function(event) {
    var id = null; // id of an existing group, if we're editing one
    var selection = null; // atoms to include in a newly created group
    if (this._lassoHelper.running()) { // TODO it catches more events than needed, to be re-factored
        selection = this._lassoHelper.end(event);
    } else {
        var ci = this.editor.render.findItem(event, this.maps);
        if (!ci || ci.type == 'Canvas')
            return;
        this._hoverHelper.hover(null);

        if (ci.map == 'atoms') {
            // if we click the SGroup tool on a single atom or bond, make a group out of those
            selection = {'atoms': [ci.id]};
        } else if (ci.map == 'bonds') {
            var bond = this.editor.render.ctab.bonds.get(ci.id);
            selection = {'atoms': [bond.b.begin, bond.b.end]};
        } else if (ci.map == 'sgroups') {
            id = ci.id;
        } else {
            return;
        }
    }
    // TODO: handle click on an existing group?
    if (id != null || (selection && selection.atoms && selection.atoms.length > 0))
        this._sGroupHelper.showPropertiesDialog(id, selection);
};

rnd.Editor.SGroupTool.SGroupHelper.prototype.postClose = function() {
    this.editor.ui.updateSelection();
    this.editor.ui.updateClipboardButtons(); // TODO review
    this.editor.render.update();
};

rnd.Editor.SGroupTool.SGroupHelper.prototype.OnPropertiesDialogOk = function(id, type, attrs) {
    if (id == null) {
        id = ui.render.ctab.molecule.sgroups.newId();
        this.editor.ui.addUndoAction(this.editor.ui.Action.fromSgroupAddition(type, this.selection.atoms, attrs, id), true);
    } else {
        this.editor.ui.addUndoAction(this.editor.ui.Action.fromSgroupAttrs(id, attrs).mergeWith(this.editor.ui.Action.fromSgroupType(id, type)), true);
    }
    this.postClose();
};

rnd.Editor.SGroupTool.SGroupHelper.prototype.OnPropertiesDialogCancel = function() {
    this.postClose();
};

rnd.Editor.PasteTool = function(editor) {
    this.editor = editor;
    this.action = this.editor.ui.Action.fromPaste(
        this.editor.ui.clipboard,
        'lastEvent' in this.OnMouseMove0
            ? util.Vec2.diff(
                this.editor.ui.page2obj(this.OnMouseMove0.lastEvent),
                this.editor.ui.clipboard.getAnchorPosition())
            : undefined
    );
    this.editor.render.update();
};
rnd.Editor.PasteTool.prototype = new rnd.Editor.EditorTool();
rnd.Editor.PasteTool.prototype.OnMouseMove = function(event) {
    this.action.perform(this.editor);
    this.action = this.editor.ui.Action.fromPaste(
        this.editor.ui.clipboard,
        util.Vec2.diff(this.editor.ui.page2obj(event), this.editor.ui.clipboard.getAnchorPosition())
    );
    this.editor.render.update();
};
rnd.Editor.PasteTool.prototype.OnMouseUp = function() {
    this.editor.ui.addUndoAction(this.action);
    delete this.action;
    this.editor.ui.selectMode(this.editor.ui.defaultSelector);
};
rnd.Editor.PasteTool.prototype.OnCancel = function() {
    if ('action' in this) {
        this.action.perform(this.editor);
        delete this.action;
    }
};

/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.Prototype)
	throw new Error("Prototype.js should be loaded first");
if (!window.rnd)
	throw new Error("rnd should be defined prior to loading this file");

rnd.ElementTable = function (clientArea, opts, isTable)
{
	opts = opts || {};
	clientArea = p$(clientArea);
	clientArea.innerHTML = "";
	var table = this;
	this.onClick = opts.onClick || function(elemNum){
		if (this.mode == 'single')
			table.setElementSingle(elemNum);
		else
			table.setElementSelected(elemNum, !table.items[elemNum].selected);
		table.updateAtomProps();
	};

	var hsz = opts.buttonHalfSize || 16;
	this.elemHalfSz = new util.Vec2(hsz, hsz);
	this.elemSz = this.elemHalfSz.scaled(2);
	this.spacing = new util.Vec2(3, 3);
	this.cornerRadius = 0;
	this.orig = this.elemSz.scaled(0);
	this.mode = 'single';

	if (isTable) {
		this.size = new util.Vec2((this.elemSz.x + this.spacing.x) * 18 + this.spacing.x, (this.elemSz.y + this.spacing.y) *9 + this.spacing.y);
		clientArea.style.width = (this.size.x).toString() + 'px';
		clientArea.style.height = (this.size.y).toString() + 'px';
	}
	this.viewSz = new util.Vec2(clientArea['clientWidth'] || 100, clientArea['clientHeight'] || 100);

	this.paper = new Raphael(clientArea, this.viewSz.x, this.viewSz.y);
	this.bb = new util.Box2Abs(new util.Vec2(), this.viewSz);

	this.fillColor = opts.fillColor || '#def';
	this.fillColorSelected = opts.fillColorSelected || '#fcb';
	this.frameColor = opts.frameColor || '#9ad';
	this.frameThickness = opts.frameThickness || '1pt';
	this.fontSize = opts.fontSize || 19;
	this.fontType = opts.fontType || "Arial";

	this.frameAttrs = {
			'fill':this.fillColor,
			'stroke':this.frameColor,
			'stroke-width':this.frameThickness
		};
	this.fontAttrs = {
			'font-family': this.fontType,
			'font-size': this.fontSize
		};
	this.items = {};
	this.selectedLabels = util.Set.empty();
	this.singleLabel = -1;
	this.atomProps = {};
};

rnd.ElementTable.prototype.updateAtomProps = function () {
	this.atomProps = {};
	if (this.mode == 'single') {
		if (this.singleLabel < 0)
			return;
		this.atomProps.label = chem.Element.elements.get(this.singleLabel).label;
	} else {
		if (util.Set.size(this.selectedLabels) == 0)
			return;
		var notList = this.mode == 'notlist';
		var ids = util.Set.list(this.selectedLabels);
		ids.sort(function(a, b){return a-b;});
		this.atomProps = {
			'label':'',
			'atomList': new chem.Struct.AtomList({
					'notList': notList,
					'ids': ids
			})
		};
	}
};

rnd.ElementTable.prototype.getAtomProps = function () {
	return this.atomProps;
};

rnd.ElementTable.prototype.renderTable = function () {
	var table = this;
	chem.Element.elements.each(function(id, elem){
		var centre = new util.Vec2(this.orig.x + (elem.xpos - 1) * (this.spacing.x + this.elemSz.x) + this.elemHalfSz.x + this.spacing.x, this.orig.y + (elem.ypos - 1) * (this.spacing.y + this.elemSz.y) + this.elemHalfSz.y + this.spacing.y);
		var box = this.paper.rect(centre.x - this.elemHalfSz.x, centre.y - this.elemHalfSz.y, this.elemSz.x, this.elemSz.y, this.cornerRadius).attr(this.frameAttrs);
		var label = this.paper.text(centre.x, centre.y, elem.label).attr(this.fontAttrs).attr('fill', elem.labelColor);
		box.node.onclick = function () {table.onClick(id);};
		label.node.onclick = function () {table.onClick(id);};
		this.items[id] = {'box':box, 'label':label, 'selected':false};
	}, this);
};

rnd.ElementTable.prototype.renderSingle = function (element) {
	var elemId = chem.Element.getElementByLabel(element);
	var elem = chem.Element.elements.get(elemId);
	this.items[element] = this.paper.text(this.viewSz.x / 2, this.viewSz.y / 2, element).attr(this.fontAttrs).attr('fill', elem ? elem.color : '#000');
};

rnd.ElementTable.prototype.renderArrow = function () {
	var margin = 4, hsz = 16, hext = 6, hw = 4;
	this.items['arrow'] = this.paper.path("M{1},{3}L{2},{4}L{1},{5}M{0},{4}L{2},{4}", margin, 2 * hsz - hext - margin, 2 * hsz - margin, hsz - hw, hsz, hsz + hw).attr({'stroke': '#000','stroke-width': '2px'});
};

rnd.ElementTable.prototype.renderPlus = function () {
	var hsz = 16, hext = 9;
	this.items['plus'] = this.paper.path("M{1},{0}L{1},{2}M{0},{1}L{2},{1}", hsz - hext, hsz, hsz + hext).attr({'stroke': '#000','stroke-width': '2px'});
};

rnd.ElementTable.prototype.markSelected = function (id, selected) {
	var item = this.items[id];
	if (selected) {
		item.box.attr('fill',this.fillColorSelected);
	} else {
		item.box.attr('fill',this.fillColor);
	}
	item.selected = selected;
}

rnd.ElementTable.prototype.setElementSingle = function (id) {
	if (this.singleLabel >= 0)
		this.markSelected(this.singleLabel, false);
	if (id >= 0)
		this.markSelected(id, true);
	this.singleLabel = id;
};

rnd.ElementTable.prototype.setElementSelected = function (id, selected) {
	this.markSelected(id, selected);
	if (selected) {
		util.Set.add(this.selectedLabels, id);
	} else {
		util.Set.remove(this.selectedLabels, id);
	}
};

rnd.ElementTable.prototype.setMode = function (mode) {
	if (mode == 'single') {
		util.Set.each(this.selectedLabels, function(id){
			this.markSelected(id, false);
		}, this);
		this.selectedLabels = util.Set.empty();
	} else {
		this.setElementSingle(-1);
	}
	this.mode = mode;
	this.updateAtomProps();
}

rnd.ElementTable.prototype.store = function () {
	this.old = {
		'selectedLabels' : util.Set.clone(this.selectedLabels),
		'singleLabel': this.singleLabel,
		'mode' : this.mode
	};
}

rnd.ElementTable.prototype.restore = function () {
	util.Set.each(this.selectedLabels, function(id){
		this.markSelected(id, false);
	}, this);
	if (this.singleLabel >= 0)
		this.markSelected(this.singleLabel, false);

	this.setMode(this.old.mode);
	if (this.old.mode == 'single') {
		this.setElementSingle(this.old.singleLabel);
	} else {
		util.Set.each(this.old.selectedLabels, function(id){
			this.markSelected(id, true);
		}, this);
		this.selectedLabels = util.Set.clone(this.old.selectedLabels);
	}
        p$('elem_table_'+this.old.mode).checked=true;
	this.updateAtomProps();
}
/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (!window.Prototype)
	throw new Error("Prototype.js should be loaded first");
if (!window.rnd)
	throw new Error("rnd should be defined prior to loading this file");

rnd.RGroupTable = function (clientArea, opts, isTable)
{
	opts = opts || {};
	clientArea = p$(clientArea);
	clientArea.innerHTML = "";
	this.onClick = opts.onClick || function(rgi){
        this.setSelection(this.mode == 'single'
            ? this.selection == (1 << rgi) ? 0 : (1 << rgi)
            : (this.selection ^ (1 << rgi)) & 0xFFFFFFFF
        );
	};

	var hsz = opts.buttonHalfSize || 16;
	this.elemHalfSz = new util.Vec2(hsz, hsz);
	this.elemSz = this.elemHalfSz.scaled(2);
	this.spacing = new util.Vec2(3, 3);
	this.cornerRadius = 0;
	this.orig = this.elemSz.scaled(0);
	this.mode = opts.mode || 'multiple';
    this.selection = 0;

	if (isTable) {
		this.size = new util.Vec2(
            (this.elemSz.x + this.spacing.x) * 8 + this.spacing.x,
            (this.elemSz.y + this.spacing.y) * 4 + this.spacing.y
        );
		clientArea.style.width = (this.size.x).toString() + 'px';
		clientArea.style.height = (this.size.y).toString() + 'px';
	}
	this.viewSz = new util.Vec2(clientArea['clientWidth'] || 100, clientArea['clientHeight'] || 100);

	this.paper = new Raphael(clientArea, this.viewSz.x, this.viewSz.y);
	this.bb = new util.Box2Abs(new util.Vec2(), this.viewSz);

	this.fillColor = opts.fillColor || '#def';
	this.fillColorSelected = opts.fillColorSelected || '#fcb';
	this.frameColor = opts.frameColor || '#9ad';
	this.frameThickness = opts.frameThickness || '1pt';
	this.fontSize = opts.fontSize || 18;
	this.fontType = opts.fontType || "Arial";

	this.frameAttrs = {
        'fill':this.fillColor,
        'stroke':this.frameColor,
        'stroke-width':this.frameThickness
    };
	this.fontAttrs = {
        'font-family': this.fontType,
        'font-size': this.fontSize
    };
    this.items = [];

    for (var rgi = 0; rgi < 32; rgi++) {
        (function(rgi) {
            var center = new util.Vec2(
                this.orig.x + (rgi % 8) * (this.spacing.x + this.elemSz.x) + this.elemHalfSz.x + this.spacing.x,
                this.orig.y + parseInt(rgi / 8) * (this.spacing.y + this.elemSz.y) + this.elemHalfSz.y + this.spacing.y
            );
            var box = this.paper.rect(
                center.x - this.elemHalfSz.x, center.y - this.elemHalfSz.y,
                this.elemSz.x, this.elemSz.y, this.cornerRadius
            ).attr(this.frameAttrs);
            var label = this.paper.text(center.x, center.y, 'R' + (rgi + 1).toString()).attr(this.fontAttrs);
            var self = this;
            box.node.onclick = function() {
                self.onClick(rgi);
            };
            label.node.onclick = function() {
                self.onClick(rgi);
            };
            this.items[rgi] = { box : box, label : label };
        }).apply(this, [rgi]);
    }
};

rnd.RGroupTable.prototype.setMode = function(mode) {
    this.mode = mode;
};

rnd.RGroupTable.prototype.setSelection = function(selection) {
    this.selection = selection;
    for (var rgi = 0; rgi < 32; rgi++) {
        this.items[rgi].box.attr('fill', this.selection & (1 << rgi) ? this.fillColorSelected : this.fillColor);
    }
};



/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (typeof(ui) == 'undefined')
    ui = function () {};

ui.standalone = true;

ui.path = '/';
ui.base_url = '';

ui.scale = 40;

ui.zoomValues = [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0];
ui.zoomIdx = ui.zoomValues.indexOf(1.0);
ui.zoom = 1.0;

ui.DBLCLICK_INTERVAL = 300;

ui.HISTORY_LENGTH = 8;

ui.DEBUG = false;

ui.render = null;

ui.ctab = new chem.Struct();

ui.client_area = null;
ui.mode_id = null;

ui.undoStack = new Array();
ui.redoStack = new Array();

ui.is_osx = false;
ui.is_touch = false;
ui.initialized = false;

ui.MODE = {SIMPLE: 1, ERASE: 2, ATOM: 3, BOND: 4, PATTERN: 5, SGROUP: 6, PASTE: 7, CHARGE: 8, RXN_ARROW: 9, RXN_PLUS: 10, CHAIN: 11};

//
// Init section
//
ui.initButton = function (el)
{
    el.observe(EventMap['mousedown'], function (event)
    {
        if (this.hasClassName('buttonDisabled'))
            return;
        this.addClassName('buttonPressed');
        // manually toggle off all active dropdowns
        ui.hideBlurredControls();
        util.stopEventPropagation(event);
    });
    el.observe(EventMap['mouseup'], function ()
    {
        this.removeClassName('buttonPressed');
    });
    el.observe('mouseover', function ()
    {
        if (this.hasClassName('buttonDisabled'))
            return;
        this.addClassName('buttonHighlight');

        var status = this.getAttribute('title');
        if (status != null)
            window.status = status;
    });
    el.observe('mouseout', function ()
    {
        this.removeClassName('buttonPressed');
        this.removeClassName('buttonHighlight');
        window.status = '';
    });
};

ui.onClick_SideButton = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;
    if (this.hasClassName('stateButton') && this.hasClassName('buttonSelected'))
    {
        ui.toggleDropdownList(this.id + '_dropdown');
    } else {
        ui.selectMode(this.getAttribute('selid') || this.id);
    }
};

ui.onClick_DropdownButton = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;
    ui.toggleDropdownList(this.id);
};

ui.onMouseDown_DropdownListItem = function (event)
{
    ui.selectMode(this.id);
    var dropdown_mode_id = this.id.split('_')[0];
    p$(dropdown_mode_id + '_dropdown_list').hide();
    if (ui.mode_id == this.id)
    {
        if (p$(dropdown_mode_id).getAttribute('src')) {
            p$(dropdown_mode_id).setAttribute('src', this.select('img')[0].getAttribute('src'));
        } else {
            ketcher.showMolfileOpts(dropdown_mode_id, ketcher.templates[ui.mode_id], 20, {
                'autoScale':true,
                'autoScaleMargin':4,
                'hideImplicitHydrogen':true,
                'hideTerminalLabels':true,
                'ignoreMouseEvents':true
            });
        }
        p$(dropdown_mode_id).title = this.title;
        p$(dropdown_mode_id).setAttribute('selid', ui.mode_id);
    }
    if (event)
    {
        util.stopEventPropagation(event);
        return util.preventDefault(event);
    }
};

ui.defaultSelector = 'selector_lasso';

ui.init = function (settings)
{
    if (this.initialized)
    {
        this.Action.fromNewCanvas(new chem.Struct());
        this.render.update();
        this.undoStack.clear();
        this.redoStack.clear();
        this.updateActionButtons();
        this.selectMode(ui.defaultSelector);
        return;
    }

    if (!settings)
      settings = {};

    this.root = (!!settings.root ? settings.root : document);
    this.is_osx = (navigator.userAgent.indexOf('Mac OS X') != -1);
    this.is_touch = 'ontouchstart' in document;
    
    if (settings.ajaxRequest === undefined) {
      ui.ajaxRequest = function (service, method, async, parameters, onready) {
        new Ajax.Request(ui.path + service,
        {
          'method': method,
          'asynchronous' : async,
          'parameters' : parameters,
          'onComplete': function (res) { onready(res.responseText, res); }
        });
      }
    }
    else
      ui.ajaxRequest = settings.ajaxRequest;

    // IE specific styles
    if (Prototype.Browser.IE)
    {
        $$('.chemicalText').each(function (el)
        {
            el.addClassName('chemicalText_IE');
        });

        // IE6
        if (navigator.userAgent.indexOf('MSIE 6.0') != -1)
        {
            $$('.dialogWindow').each(function (dlg)
            {
                dlg.style.width = "300px";
            });
        }
    }

    // OS X specific stuff
    if (ui.is_osx)
    {
        $$('.toolButton, .toolButton > img, .sideButton').each(function (button)
        {
            button.title = button.title.replace("Ctrl", "Cmd");
        }, this);
    }
    
    // Touch device stuff
    if (ui.is_touch)
    {
        EventMap =
        {
            mousemove: 'touchmove',
            mousedown: 'touchstart',
            mouseup  : 'touchend'
        };
        
        // to enable copy to clipboard on iOS
        p$('output_mol').removeAttribute('readonly');
        
        // rbalabanov: here is temporary fix for "drag issue" on iPad
        //BEGIN
        rnd.ReStruct.prototype.hiddenPaths = [];
        
        rnd.ReStruct.prototype.clearVisel = function (visel)
        {
            for (var i = 0; i < visel.paths.length; ++i) {
                visel.paths[i].hide();
                this.hiddenPaths.push(visel.paths[i]);
            }
            visel.clear();
        };
        //END
    }

    // Document events
    p$(this.root).observe('keypress', ui.onKeyPress_Ketcher);
    p$(this.root).observe('keydown', ui.onKeyDown_IE);
    p$(this.root).observe('keyup', ui.onKeyUp);
    p$(this.root).observe(EventMap['mousedown'], ui.onMouseDown_Ketcher);
    p$(this.root).observe(EventMap['mouseup'], ui.onMouseUp_Ketcher);

    // Button events
    $$('.toolButton').each(ui.initButton);
    $$('.modeButton').each(function (el)
    {
        ui.initButton(el);
        if (el.identify() != 'atom_table' && el.identify() != 'atom_reagenerics')
            el.observe('click', ui.onClick_SideButton); // TODO need some other way, in general tools should be pluggable
    });
    $$('.dropdownButton').each(function (el)
    {
        el.observe('click', ui.onClick_DropdownButton);
    });
    $$('.dropdownListItem').each(function (el)
    {
        el.observe(EventMap['mousedown'], ui.onMouseDown_DropdownListItem);
        el.observe('mouseover', function ()
        {
            this.addClassName('highlightedItem');
        });
        el.observe('mouseout', function ()
        {
            this.removeClassName('highlightedItem');
        });
    });
    p$('new').observe('click', ui.onClick_NewFile);
    p$('open').observe('click', ui.onClick_OpenFile);
    p$('save').observe('click', ui.onClick_SaveFile);
    p$('undo').observe('click', ui.onClick_Undo);
    p$('redo').observe('click', ui.onClick_Redo);
    p$('cut').observe('click', ui.onClick_Cut);
    p$('copy').observe('click', ui.onClick_Copy);
    p$('paste').observe('click', ui.onClick_Paste);
    p$('zoom_in').observe('click', ui.onClick_ZoomIn);
    p$('zoom_out').observe('click', ui.onClick_ZoomOut);
    p$('clean_up').observe('click', ui.onClick_CleanUp);
    p$('aromatize').observe('click', ui.onClick_Aromatize);
    p$('dearomatize').observe('click', ui.onClick_Dearomatize);
    p$('atom_table').observe('click', ui.onClick_ElemTableButton);
    p$('elem_table_list').observe('click', ui.onSelect_ElemTableNotList);
    p$('elem_table_notlist').observe('click', ui.onSelect_ElemTableNotList);
    p$('atom_reagenerics').observe('click', ui.onClick_ReaGenericsTableButton); // TODO need some other way, in general tools should be pluggable

    // Client area events
    this.client_area = p$('client_area');
    this.client_area.observe('scroll', ui.onScroll_ClientArea);

    // Dialog events
    $$('.dialogWindow').each(function (el)
    {
        el.observe('keypress', ui.onKeyPress_Dialog);
        el.observe('keyup', ui.onKeyUp);
    });

    // Atom properties dialog events
    p$('atom_label').observe('change', ui.onChange_AtomLabel);
    p$('atom_charge').observe('change', ui.onChange_AtomCharge);
    p$('atom_isotope').observe('change', ui.onChange_AtomIsotope);
    p$('atom_valence').observe('change', ui.onChange_AtomValence);
    p$('atom_prop_cancel').observe('click', function ()
    {
        ui.hideDialog('atom_properties');
    });
    p$('atom_prop_ok').observe('click', function ()
    {
        ui.applyAtomProperties();
    });
    p$('bond_prop_cancel').observe('click', function ()
    {
        ui.hideDialog('bond_properties');
    });
    p$('bond_prop_ok').observe('click', function ()
    {
        ui.applyBondProperties();
    });

    // S-group properties dialog events
    p$('sgroup_type').observe('change', ui.onChange_SGroupType);
    p$('sgroup_label').observe('change', ui.onChange_SGroupLabel);

    // Label input events
    p$('input_label').observe('blur', function ()
    {
        this.hide();
    });
    p$('input_label').observe('keypress', ui.onKeyPress_InputLabel);
    p$('input_label').observe('keyup', ui.onKeyUp);

    // Element table
    p$('elem_table_cancel').observe('click', function ()
    {
        ui.elem_table_obj.restore();
        ui.hideDialog('elem_table');
    });
    p$('elem_table_ok').observe('click', function (event)
    {
        ui.hideDialog('elem_table');
        ui.onClick_SideButton.apply(p$('atom_table'), [event]);
    });

    // Load dialog events
    p$('radio_open_from_input').observe('click', ui.onSelect_OpenFromInput);
    p$('radio_open_from_file').observe('click', ui.onSelect_OpenFromFile);
    p$('input_mol').observe('keyup', ui.onChange_Input);
    p$('input_mol').observe('click', ui.onChange_Input);
    p$('read_cancel').observe('click', function ()
    {
        ui.hideDialog('open_file');
    });
    p$('read_ok').observe('click', function ()
    {
        ui.loadMoleculeFromInput();
    });
    p$('upload_mol').observe('submit', function ()
    {
        ui.hideDialog('open_file');
    });
    p$('upload_cancel').observe('click', function ()
    {
        ui.hideDialog('open_file');
    });

    // Save dialog events
    p$('file_format').observe('change', ui.onChange_FileFormat);
    p$('save_ok').observe('click', function ()
    {
        ui.hideDialog('save_file');
    });

    ui.onResize_Ketcher();
    if (Prototype.Browser.IE)
    {
        ui.client_area.absolutize(); // Needed for clipping and scrollbars in IE
        p$('ketcher_window').observe('resize', ui.onResize_Ketcher);
    }

    ui.path = document.location.pathname.substring(0, document.location.pathname.lastIndexOf('/') + 1);
    ui.base_url = document.location.href.substring(0, document.location.href.lastIndexOf('/') + 1);

    ui.ajaxRequest('knocknock', 'get', false, null, function (res, xhr) {
      if (res == 'You are welcome!')
          ui.standalone = false;
    });

    if (this.standalone)
    {
        $$('.serverRequired').each(function (el)
        {
            if (el.hasClassName('toolButton'))
                el.addClassName('buttonDisabled');
            else
                el.hide();
        });
    } else
    {
        if (ui.path != '/')
        {
            p$('upload_mol').action = ui.base_url + 'open';
            p$('download_mol').action = ui.base_url + 'save';
        }
    }

    // Init renderer
    this.render =  new rnd.Render(this.client_area, ui.scale, {atomColoring: true});
    this.editor = new rnd.Editor(this.render);

    this.selectMode('selector_lasso');

    this.render.onCanvasOffsetChanged = this.onOffsetChanged;

    this.render.setMolecule(this.ctab);
    this.render.update();

    this.initialized = true;
};

ui.showDialog = function (name)
{
    p$('window_cover').style.width = p$('ketcher_window').getWidth().toString() + 'px';
    p$('window_cover').style.height = p$('ketcher_window').getHeight().toString() + 'px';
    p$('window_cover').show();
    p$(name).show();
};

ui.hideDialog = function (name)
{
    p$(name).hide();
    p$('window_cover').hide();
    p$('window_cover').style.width = '0px';
    p$('window_cover').style.height = '0px';
};

ui.toggleDropdownList = function (name)
{
    var list_id = name + '_list';
    if (p$(list_id).visible())
        p$(list_id).hide();
    else
    {
        p$(list_id).show();
        if (p$(list_id).hasClassName('renderFirst'))
        {
            var renderOpts = {
                'autoScale':true,
                'autoScaleMargin':4,
                'hideImplicitHydrogen':true,
                'hideTerminalLabels':true
            };

            p$(list_id).select("tr").each(function (item)
            {
                if (p$(item.id + '_preview'))
                    ketcher.showMolfileOpts(item.id + '_preview', ketcher.templates[item.id], 20, renderOpts);
            });

            p$(list_id).removeClassName('renderFirst');
        }
    }
};


ui.onResize_Ketcher = function ()
{
    if (Prototype.Browser.IE)
        ui.client_area.style.width = (Element.getWidth(ui.client_area.parentNode) - 2).toString() + 'px';

    ui.client_area.style.height = (Element.getHeight(ui.client_area.parentNode) - 2).toString() + 'px';
};

//
// Main section
//
ui.updateMolecule = function (mol)
{
    if (typeof(mol) == 'undefined' || mol == null)
        return;

    if (ui.selected())
        ui.updateSelection();

    this.addUndoAction(this.Action.fromNewCanvas(mol));

    ui.showDialog('loading');
    setTimeout(function ()
    {
        try
        {
            ui.render.onResize(); // TODO: this methods should be called in the resize-event handler
            ui.render.update();
            ui.setZoomCentered(null, ui.render.getStructCenter());
        } catch (er)
        {
            alert(er.message);
        } finally
        {
            ui.hideDialog('loading');
        }
    }, 50);
};

ui.parseCTFile = function (molfile, check_empty_line)
{
    var lines = molfile.split('\n');

    if (lines.length > 0 && lines[0] == 'Ok.')
        lines.shift();

    try
    {
        try {
            return chem.Molfile.parseCTFile(lines);
        } catch (ex) {
            if (check_empty_line) {
                try {
                // check whether there's an extra empty line on top
                // this often happens when molfile text is pasted into the dialog window
                    return chem.Molfile.parseCTFile(lines.slice(1));
                } catch (ex1) {}
                try {
                // check for a missing first line
                // this sometimes happens when pasting
                    return chem.Molfile.parseCTFile([''].concat(lines));
                } catch (ex2) {}
            }
            throw ex;
        }
    } catch (er)
    {
        alert("Error loading molfile.\n"+er.toString());
        return null;
    }
};

//
// Mode functions
//
ui.selectMode = function (mode)
{
    if (mode == 'reaction_automap') {
        ui.showAutomapProperties({
            onOk: function(mode) {
                var moldata = new chem.MolfileSaver().saveMolecule(ui.ctab/*.clone()*/, true);
                ui.ajaxRequest('automap', 'post', true, { moldata : moldata, mode : mode }, function (res) {
                  if (res.startsWith('Ok.')) {
                      ui.updateMolecule(ui.parseCTFile(res));
                  }
              });
            }
        });
        return;
    }

    if (mode != null)
    {
        if (p$(mode).hasClassName('buttonDisabled'))
            return;

        if (ui.selected()) {
            if (mode == 'select_erase') {
                ui.removeSelected();
                return;
            }
            // BK: TODO: add this ability to mass-change atom labels to the keyboard handler
            if (mode.startsWith('atom_')) {
                ui.addUndoAction(ui.Action.fromSelectedAtomsAttrs(ui.atomLabel(mode)), true);
                ui.render.update();
                return;
            }
        }
        /* BK: TODO: add this ability to change the bond under cursor to the editor tool
        else if (mode.startsWith('bond_')) {
            var cBond = ui.render.findClosestBond(ui.page2obj(ui.cursorPos));
            if (cBond) {
                ui.addUndoAction(ui.Action.fromBondAttrs(cBond.id, { type: ui.bondType(mode).type, stereo: chem.Struct.BOND.STEREO.NONE }), true);
                ui.render.update();
                return;
            }
        } */
    }

    if (this.mode_id != null && this.mode_id != mode) {
        var button_id = this.mode_id.split('_')[0];
        var state_button = (p$(button_id) && p$(button_id).hasClassName('stateButton')) || false;

        if (state_button) {
            if (mode && !mode.startsWith(button_id))
                p$(button_id).removeClassName('buttonSelected');
        } else
            p$(this.mode_id).removeClassName('buttonSelected');
    }

    this.editor.deselectAll();

    if (this.render.current_tool)
        this.render.current_tool.OnCancel();

    if (mode == null) {
        this.mode_id = null;
        delete this.render.current_tool;
    } else {
        this.render.current_tool = this.editor.toolFor(mode);
        this.mode_id = mode;

        button_id = this.mode_id.split('_')[0];
        state_button = (p$(button_id) && p$(button_id).hasClassName('stateButton')) || false;

        if (state_button)
            p$(button_id).addClassName('buttonSelected');
        else
            p$(this.mode_id).addClassName('buttonSelected');
    }
};

ui.bondTypeMap = {
    'single'   : {type: 1, stereo: chem.Struct.BOND.STEREO.NONE},
    'up'       : {type: 1, stereo: chem.Struct.BOND.STEREO.UP},
    'down'     : {type: 1, stereo: chem.Struct.BOND.STEREO.DOWN},
    'updown'   : {type: 1, stereo: chem.Struct.BOND.STEREO.EITHER},
    'double'   : {type: 2, stereo: chem.Struct.BOND.STEREO.NONE},
    'crossed'  : {type: 2, stereo: chem.Struct.BOND.STEREO.CIS_TRANS},
    'triple'   : {type: 3, stereo: chem.Struct.BOND.STEREO.NONE},
    'aromatic' : {type: 4, stereo: chem.Struct.BOND.STEREO.NONE},
    'singledouble'   : {type: 5, stereo: chem.Struct.BOND.STEREO.NONE},
    'singlearomatic' : {type: 6, stereo: chem.Struct.BOND.STEREO.NONE},
    'doublearomatic' : {type: 7, stereo: chem.Struct.BOND.STEREO.NONE},
    'any'      :  {type: 8, stereo: chem.Struct.BOND.STEREO.NONE}
};

ui.bondType = function (mode)
{
    var type_str;

    if (Object.isUndefined(mode))
        type_str = ui.mode_id.substr(5);
    else
        type_str = mode.substr(5);

    return ui.bondTypeMap[type_str];
};

ui.atomLabel = function (mode)
{
    var label;

    if (Object.isUndefined(mode))
        label = ui.mode_id.substr(5);
    else
        label = mode.substr(5);

    if (label == 'table')
        return ui.elem_table_obj.getAtomProps();
    if (label == 'reagenerics') // TODO need some other way, in general tools should be pluggable
        return ui.reagenerics_table_obj.getAtomProps();
    if (label == 'any')
        return {'label':'A'};
    else
        return {'label':label.capitalize()};
};

//
// New document
//
ui.onClick_NewFile = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    ui.selectMode(ui.defaultSelector);

    if (!ui.ctab.isBlank())
    {
        ui.addUndoAction(ui.Action.fromNewCanvas(new chem.Struct()));
        ui.render.update();
    }
};

//
// Hot keys
//
ui.onKeyPress_Ketcher = function (event)
{
    util.stopEventPropagation(event);

    if (p$('window_cover').visible())
        return util.preventDefault(event);

    //rbalabanov: here we try to handle event using current editor tool
    //BEGIN
    if (ui && ui.render.current_tool) {
        if (ui.render.current_tool.processEvent('OnKeyPress', event)) {
            return util.preventDefault(event);
        }
    }
    //END

    switch (Prototype.Browser.IE ? event.keyCode : event.which)
    {
    case 43: // +
    case 61:
        ui.onClick_ZoomIn.call(p$('zoom_in'));
        return util.preventDefault(event);
    case 45: // -
    case 95:
        ui.onClick_ZoomOut.call(p$('zoom_out'));
        return util.preventDefault(event);
    case 8: // Back space
        if (ui.is_osx && ui.selected())
            ui.removeSelected();
        return util.preventDefault(event);
    case 48: // 0
        ui.onMouseDown_DropdownListItem.call(p$('bond_any'));
        return util.preventDefault(event);
    case 49: // 1
        var singles = ['bond_single', 'bond_up', 'bond_down', 'bond_updown'];
        ui.onMouseDown_DropdownListItem.call(p$(singles[(singles.indexOf(ui.mode_id) + 1) % singles.length]));
        return util.preventDefault(event);
    case 50: // 2
        var doubles = ['bond_double', 'bond_crossed'];
        ui.onMouseDown_DropdownListItem.call(p$(doubles[(doubles.indexOf(ui.mode_id) + 1) % doubles.length]));
        return util.preventDefault(event);
    case 51: // 3
        ui.onMouseDown_DropdownListItem.call(p$('bond_triple'));
        return util.preventDefault(event);
    case 52: // 4
        ui.onMouseDown_DropdownListItem.call(p$('bond_aromatic'));
        return util.preventDefault(event);
    case 53: // 5
        var charge = ['charge_plus', 'charge_minus'];
        ui.selectMode(charge[(charge.indexOf(ui.mode_id) + 1) % charge.length]);
        return util.preventDefault(event);
    case 66: // Shift+B
        ui.selectMode('atom_br');
        return util.preventDefault(event);
    case 67: // Shift+C
        ui.selectMode('atom_cl');
        return util.preventDefault(event);
    case 82: // Shift+R
        ui.selectMode('rgroup');
        return util.preventDefault(event);
    case 90: // Ctrl+Shift+Z
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_Redo.call(p$('redo'));
        return util.preventDefault(event);
    case 97: // a
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.selectAll();
        else
            ui.selectMode('atom_any');
        return util.preventDefault(event);
    case 99: // c
        if (!event.altKey)
        {
            if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
                ui.onClick_Copy.call(p$('copy'));
            else if (!event.metaKey)
                ui.selectMode('atom_c');
        }
        return util.preventDefault(event);
    case 102: // f
        ui.selectMode('atom_f');
        return util.preventDefault(event);
    case 103: // Ctrl+G
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_SideButton.call(p$('sgroup'));
        return util.preventDefault(event);
    case 104: // h
        ui.selectMode('atom_h');
        return util.preventDefault(event);
    case 105: // i
        ui.selectMode('atom_i');
        return util.preventDefault(event);
    case 108: // Ctrl+L
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_CleanUp.call(p$('clean_up'));
        return util.preventDefault(event);
    case 110: // n or Ctrl+N
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_NewFile.call(p$('new'));
        else
            ui.selectMode('atom_n');
        return util.preventDefault(event);
    case 111: // o or Ctrl+O
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_OpenFile.call(p$('open'));
        else
            ui.selectMode('atom_o');
        return util.preventDefault(event);
    case 112: // p
        ui.selectMode('atom_p');
        return util.preventDefault(event);
    case 115: // s or Ctrl+S
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_SaveFile.call(p$('save'));
        else
            ui.selectMode('atom_s');
        return util.preventDefault(event);
    case 116: // t
        if (ui.mode_id.startsWith('template_')) {
            var templates = rnd.Editor.TemplateTool.prototype.templates;
            ui.onMouseDown_DropdownListItem.apply(
                { id : 'template_' + (parseInt(ui.mode_id.split('_')[1]) + 1) % templates.length }
            );
        } else {
            ui.onMouseDown_DropdownListItem.apply({ id : p$('template').getAttribute('selid') });
        }
        return util.preventDefault(event);
    case 118: // Ctrl+V
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_Paste.call(p$('paste'));
        return util.preventDefault(event);
    case 120: // Ctrl+X
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_Cut.call(p$('cut'));
        return util.preventDefault(event);
    case 122: // Ctrl+Z or Ctrl+Shift+Z (in Safari)
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
        {
            if (event.shiftKey)
                ui.onClick_Redo.call(p$('redo'));
            else
                ui.onClick_Undo.call(p$('undo'));
        }
        return util.preventDefault(event);
    case 126: // ~
        ui.render.update(true);
        return util.preventDefault(event);
    }
};

ui.ctrlShortcuts = [65, 67, 71, 76, 78, 79, 83, 86, 88, 90];

// Button handler specially for IE to prevent default actions
ui.onKeyDown_IE = function (event)
{
    if (p$('window_cover').visible())
        return true;

    if (Prototype.Browser.Gecko && event.which == 46)
    {
        util.stopEventPropagation(event);
        return util.preventDefault(event);
    }

    if (Prototype.Browser.WebKit && ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx)) && ui.ctrlShortcuts.indexOf(event.which) != -1)
    {
        // don't handle the shrtcuts in the regular fashion, e.g. saving the page, opening a document, etc.
        util.stopEventPropagation(event);
        return util.preventDefault(event);
    }

   if (!Prototype.Browser.IE)
        return;

    // Ctrl+A, Ctrl+C, Ctrl+N, Ctrl+O, Ctrl+S, Ctrl+V, Ctrl+X, Ctrl+Z
    //if ([65, 67, 78, 79, 83, 86, 88, 90].indexOf(event.keyCode) != -1 && event.ctrlKey)
    // Ctrl+A, Ctrl+G, Ctrl+L, Ctrl+N, Ctrl+O, Ctrl+S, Ctrl+Z
    if (ui.ctrlShortcuts.indexOf(event.keyCode) != -1 && event.ctrlKey)
    {
        util.stopEventPropagation(event);
        return util.preventDefault(event);
    }
};

// Button handler specially for Safari and IE
ui.onKeyUp = function (event)
{
    // Esc
    if (event.keyCode == 27)
    {
        if (this == ui.root || !this.visible())
        {
            if (!p$('window_cover').visible())
            {
                ui.selectMode(ui.defaultSelector);
            }
        } else if (this.hasClassName('dialogWindow'))
            ui.hideDialog(this.id);
        else
            this.hide();
        util.stopEventPropagation(event);
        return util.preventDefault(event);
    }

    if (p$('window_cover').visible())
        return true;

    if (event.keyCode == 46)
    {
        if (ui.selected())
            ui.removeSelected();
        util.stopEventPropagation(event);
        return util.preventDefault(event);
    }

    if (!Prototype.Browser.WebKit && !Prototype.Browser.IE)
        return;

    if (!(Prototype.Browser.WebKit &&
        ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx)) &&
        ui.ctrlShortcuts.indexOf(event.which) != -1) && (event.keyCode != 46 && Prototype.Browser.WebKit))
        return;

    if (this != ui.root)
        return;

    util.stopEventPropagation(event);

    switch (event.keyCode)
    {
    case 46: // Delete
        if (ui.selected())
            ui.removeSelected();
        return;
    case 65: // Ctrl+A
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.selectAll();
        return;
    case 67: // Ctrl+C
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_Copy.call(p$('copy'));
        return;
    case 71: // Ctrl+G
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_SideButton.call(p$('sgroup'));
        return;
    case 76: // Ctrl+L
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_CleanUp.call(p$('clean_up'));
        return;
    case 78: // Ctrl+N
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_NewFile.call(p$('new'));
        return;
    case 79: // Ctrl+O
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_OpenFile.call(p$('open'));
        return;
    case 83: // Ctrl+S
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_SaveFile.call(p$('save'));
        return;
    case 86: // Ctrl+V
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_Paste.call(p$('paste'));
        return;
    case 88: // Ctrl+X
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
            ui.onClick_Cut.call(p$('cut'));
        return;
    case 90: // Ctrl+Z
        if ((event.metaKey && ui.is_osx) || (event.ctrlKey && !ui.is_osx))
        {
            if (event.shiftKey)
                ui.onClick_Redo.call(p$('redo'));
            else
                ui.onClick_Undo.call(p$('undo'));
        }
        return;
    }
};

ui.onKeyPress_Dialog = function (event)
{
    util.stopEventPropagation(event);
    if (event.keyCode == 27)
    {
        ui.hideDialog(this.id);
        return util.preventDefault(event);
    }
};

ui.onKeyPress_InputLabel = function (event)
{
    util.stopEventPropagation(event);
    if (event.keyCode == 13)
    {
        this.hide();

        var label = '';
        var charge = 0;
        var value_arr = this.value.toArray();

        if (this.value == '*') {
            label = 'A';
        }
        else if (this.value.match(/^[*][1-9]?[+-]$/i)) {
            label = 'A';

            if (this.value.length == 2)
                charge = 1;
            else
                charge = parseInt(value_arr[1]);

            if (value_arr[2] == '-')
                charge *= -1;
        }
        else if (this.value.match(/^[A-Z]{1,2}$/i)) {
            label = this.value.capitalize();
        }
        else if (this.value.match(/^[A-Z]{1,2}[0][+-]?$/i)) {
            if (this.value.match(/^[A-Z]{2}/i))
                label = this.value.substr(0, 2).capitalize();
            else
                label = value_arr[0].capitalize();
        }
        else if (this.value.match(/^[A-Z]{1,2}[1-9]?[+-]$/i)) {
            if (this.value.match(/^[A-Z]{2}/i))
                label = this.value.substr(0, 2).capitalize();
            else
                label = value_arr[0].capitalize();

            var match = this.value.match(/[0-9]/i);

            if (match != null)
                charge = parseInt(match[0]);
            else
                charge = 1;

            if (value_arr[this.value.length - 1] == '-')
                charge *= -1;
        }

        if (label == 'A' || label == 'Q' || label == 'X' || label == 'R' || chem.Element.getElementByLabel(label) != null)
        {
            ui.addUndoAction(ui.Action.fromAtomAttrs(this.atom_id, {label: label, charge: charge}), true);
            ui.render.update();
        }
        return util.preventDefault(event);
    }
    if (event.keyCode == 27)
    {
        this.hide();
        return util.preventDefault(event);
    }
};

//
// Open file section
//
ui.onClick_OpenFile = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;
    ui.showDialog('open_file');
    p$('radio_open_from_input').checked = true;
    p$('checkbox_open_copy').checked = false;
    ui.onSelect_OpenFromInput();
};

ui.getFile = function ()
{
    var frame_body;

    if ('contentDocument' in p$('buffer_frame'))
        frame_body = p$('buffer_frame').contentDocument.body;
    else // IE7
        frame_body = document.frames['buffer_frame'].document.body;

    return Base64.decode(frame_body.title);
};

ui.loadMolecule = function (mol_string, force_layout, check_empty_line, paste)
{
    var smiles = mol_string.strip();
    var updateFunc = paste ? function (struct) {
        struct.rescale(); 
        ui.copy(struct);
        ui.updateSelection();
        ui.selectMode('paste');
    } : ui.updateMolecule;

    if (smiles.indexOf('\n') == -1)
    {
        if (ui.standalone)
        {
            if (smiles != '')
            {
                alert('SMILES is not supported in a standalone mode.');
            }
            return;
        }
        ui.ajaxRequest('layout?smiles=' + encodeURIComponent(smiles), 'get', true, null, function (res) {
          if (res.startsWith('Ok.'))
              updateFunc.call(ui, ui.parseCTFile(res));
          else if (res.startsWith('Error.'))
              alert(res.split('\n')[1]);
          else
              throw new Error('Something went wrong' + res);
        });
    } else if (!ui.standalone && force_layout)
    {
      ui.ajaxRequest('layout', 'post', true, {moldata: mol_string}, function (res) {
          if (res.startsWith('Ok.'))
              updateFunc.call(ui, ui.parseCTFile(res));
          else if (res.startsWith('Error.'))
              alert(res.split('\n')[1]);
          else
              throw new Error('Something went wrong' + res);
      });
    } else {
        updateFunc.call(ui, ui.parseCTFile(mol_string, check_empty_line));
    }
};

ui.dearomatizeMolecule = function (mol_string, aromatize)
{
    if (!ui.standalone)
    {
      ui.ajaxRequest(
        (aromatize ? 'aromatize' : 'dearomatize'), 
        'post', 
        true, 
        {moldata: mol_string},
        function (res) {
          if (res.startsWith('Ok.')) {
              ui.updateMolecule(ui.parseCTFile(res));
          } else if (res.startsWith('Error.')) {
              alert(res.split('\n')[1]);
          } else {
              throw new Error('Something went wrong' + res);
          }
      });
    } else {
        throw new Error('Aromatization and dearomatization are not supported in the standalone mode.');
    }
};

// Called from iframe's 'onload'
ui.loadMoleculeFromFile = function ()
{
    var file = ui.getFile();
    if (file.startsWith('Ok.'))
        ui.loadMolecule(file.substr(file.indexOf('\n') + 1), false, false, p$('checkbox_open_copy').checked);
};

ui.loadMoleculeFromInput = function ()
{
    ui.hideDialog('open_file');
    ui.loadMolecule(p$('input_mol').value, false, true, p$('checkbox_open_copy').checked);
};

ui.onSelect_OpenFromInput = function ()
{
    p$('open_from_input').show();
    p$('open_from_file').hide();
    ui.onChange_Input.call(p$('input_mol'));
    p$('input_mol').activate();
};

ui.onSelect_OpenFromFile = function ()
{
    p$('open_from_file').show();
    p$('open_from_input').hide();
    p$('molfile_path').focus();
};

ui.onChange_Input = function ()
{
    var el = this;

    setTimeout(function ()
    {
        if (el.value.strip().indexOf('\n') != -1)
        {
            if (el.style.wordWrap != 'normal')
                el.style.wordWrap = 'normal';
        } else
        {
            if (el.style.wordWrap != 'break-word')
                el.style.wordWrap = 'break-word';
        }
    }, 200);
};

//
// Save file section
//
ui.onClick_SaveFile = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;
    ui.showDialog('save_file');
    ui.onChange_FileFormat(null, true);
};

ui.onChange_FileFormat = function (event, update)
{
    var output = p$('output_mol');
    var el = p$('file_format');

    if (update)
    {
        var saver = new chem.MolfileSaver();
        output.molfile = saver.saveMolecule(ui.ctab, true);

        try
        {
            saver = new chem.SmilesSaver();
            output.smiles = saver.saveMolecule(ui.ctab, true);
        } catch (er)
        {
            output.smiles = er.message;
        }
    }

    if (el.value == 'mol')
    {
        output.value = output.molfile;
        output.style.wordWrap = 'normal';
    } else // if (el.value == 'smi')
    {
        output.value = output.smiles;
        output.style.wordWrap = 'break-word';
    }

    p$('mol_data').value = el.value + '\n' + output.value;
    output.activate();
};

//
// Zoom section
//
ui.onClick_ZoomIn = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    ui.zoomIdx++;

    if (ui.zoomIdx >= ui.zoomValues.length - 1)
        this.addClassName('buttonDisabled');
    p$('zoom_out').removeClassName('buttonDisabled');
    if (ui.zoomIdx < 0 || ui.zoomIdx >= ui.zoomValues.length)
        throw new Error ("Zoom index out of range");
    ui.setZoomCentered(ui.zoomValues[ui.zoomIdx], ui.render.view2obj(ui.render.viewSz.scaled(0.5)));
    ui.render.update();
};

ui.onClick_ZoomOut = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    ui.zoomIdx--;

    if (ui.zoomIdx <= 0)
        this.addClassName('buttonDisabled');
    p$('zoom_in').removeClassName('buttonDisabled');
    if (ui.zoomIdx < 0 || ui.zoomIdx >= ui.zoomValues.length)
        throw new Error ("Zoom index out of range");
    ui.setZoomCentered(ui.zoomValues[ui.zoomIdx], ui.render.view2obj(ui.render.viewSz.scaled(0.5)));
    ui.render.update();
};

ui.setZoomRegular = function (zoom) {
    //mr: prevdent unbounded zooming
    //begin
    if (zoom < 0.1 || zoom > 10)
        return;
    //end
    ui.zoom = zoom;
    ui.render.setZoom(ui.zoom);
    // when scaling the canvas down it may happen that the scaled canvas is smaller than the view window
    // don't forget to call setScrollOffset after zooming (or use extendCanvas directly)
};

// get the size of the view window in pixels
ui.getViewSz = function () {
    return new util.Vec2(ui.render.viewSz);
};

// c is a point in scaled coordinates, which will be positioned in the center of the view area after zooming
ui.setZoomCentered = function (zoom, c) {
    if (!c)
        throw new Error("Center point not specified");
    if (zoom) {
        ui.setZoomRegular(zoom);
    }
    ui.setScrollOffset(0, 0);
    var sp = ui.render.obj2view(c).sub(ui.render.viewSz.scaled(0.5));
    ui.setScrollOffset(sp.x, sp.y);
};

// set the reference point for the "static point" zoom (in object coordinates)
ui.setZoomStaticPointInit = function (s) {
    ui.zspObj = new util.Vec2(s);
};

// vp is the point where the reference point should now be (in view coordinates)
ui.setZoomStaticPoint = function (zoom, vp) {
    ui.setZoomRegular(zoom);
    ui.setScrollOffset(0, 0);
    var avp = ui.render.obj2view(ui.zspObj);
    var so = avp.sub(vp);
    ui.setScrollOffset(so.x, so.y);
};

ui.setScrollOffset = function (x, y) {
    var cx = ui.client_area.clientWidth;
    var cy = ui.client_area.clientHeight;
    ui.render.extendCanvas(x, y, cx + x, cy + y);
    ui.client_area.scrollLeft = x;
    ui.client_area.scrollTop = y;
    ui.scrollLeft = ui.client_area.scrollLeft; // TODO: store drag position in scaled systems
    ui.scrollTop = ui.client_area.scrollTop;
};

ui.setScrollOffsetRel = function (dx, dy) {
    ui.setScrollOffset(ui.client_area.scrollLeft + dx, ui.client_area.scrollTop + dy);
};

//
// Automatic layout
//
ui.onClick_CleanUp = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;


    var ms = new chem.MolfileSaver();

    try
    {
        ui.loadMolecule(ms.saveMolecule(ui.ctab), true);
    } catch (er)
    {
        alert("Molfile: " + er.message);
    }
};

ui.onClick_Aromatize = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    var ms = new chem.MolfileSaver();

    try
    {
        ui.dearomatizeMolecule(ms.saveMolecule(ui.ctab), true);
    } catch (er)
    {
        alert("Molfile: " + er.message);
    }
};

ui.onClick_Dearomatize = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    var ms = new chem.MolfileSaver();

    try
    {
        ui.dearomatizeMolecule(ms.saveMolecule(ui.ctab), false);
    } catch (er)
    {
        alert("Molfile: " + er.message);
    }
};

//
// Interactive section
//
ui.selection =
{
    atoms: [],
    bonds: [],
    rxnArrows: [],
    rxnPluses: [],
    chiralFlags: []
};

ui.page2canvas2 = function (pos)
{
    var offset = ui.client_area.cumulativeOffset();

    return new util.Vec2(pos.pageX - offset.left, pos.pageY - offset.top);
};

ui.page2obj = function (pagePos)
{
    return ui.render.view2obj(ui.page2canvas2(pagePos));
};

ui.scrollPos = function ()
{
    return new util.Vec2(ui.client_area.scrollLeft, ui.client_area.scrollTop);
};

//
// Scrolling
//
ui.scrollLeft = null;
ui.scrollTop = null;

ui.onScroll_ClientArea = function(event)
{
    if (p$('input_label').visible())
        p$('input_label').hide();

    ui.scrollLeft = ui.client_area.scrollLeft;
    ui.scrollTop = ui.client_area.scrollTop;

    util.stopEventPropagation(event);
};

//
// Clicking
//
ui.dbl_click = false;

ui.bondFlipRequired = function (bond, attrs) {
    return attrs.type == chem.Struct.BOND.TYPE.SINGLE &&
    bond.stereo == chem.Struct.BOND.STEREO.NONE &&
    attrs.stereo != chem.Struct.BOND.STEREO.NONE &&
    ui.ctab.atoms.get(bond.begin).neighbors.length <
    ui.ctab.atoms.get(bond.end).neighbors.length;
};

// Get new atom id/label and pos for bond being added to existing atom
ui.atomForNewBond = function (id)
{
    var neighbours = new Array();
    var pos = ui.render.atomGetPos(id);

    ui.render.atomGetNeighbors(id).each(function (nei)
    {
        var nei_pos = ui.render.atomGetPos(nei.aid);

        if (util.Vec2.dist(pos, nei_pos) < 0.1)
            return;

        neighbours.push({id: nei.aid, v: util.Vec2.diff(nei_pos, pos)});
    });

    neighbours.sort(function (nei1, nei2)
    {
        return Math.atan2(nei1.v.y, nei1.v.x) - Math.atan2(nei2.v.y, nei2.v.x);
    });

    var i, max_i = 0;
    var angle, max_angle = 0;

    // TODO: impove layout: tree, ...

    for (i = 0; i < neighbours.length; i++)
    {
        angle = util.Vec2.angle(neighbours[i].v, neighbours[(i + 1) % neighbours.length].v);

        if (angle < 0)
            angle += 2 * Math.PI;

        if (angle > max_angle)
            max_i = i, max_angle = angle;
    }

    var v = new util.Vec2(1, 0);

    if (neighbours.length > 0)
    {
        if (neighbours.length == 1)
        {
            max_angle = -(4 * Math.PI / 3);

            // zig-zag
            var nei = ui.render.atomGetNeighbors(id)[0];
            if (ui.render.atomGetDegree(nei.aid) > 1)
            {
                var nei_neighbours = new Array();
                var nei_pos = ui.render.atomGetPos(nei.aid);
                var nei_v = util.Vec2.diff(pos, nei_pos);
                var nei_angle = Math.atan2(nei_v.y, nei_v.x);

                ui.render.atomGetNeighbors(nei.aid).each(function (nei_nei)
                {
                    var nei_nei_pos = ui.render.atomGetPos(nei_nei.aid);

                    if (nei_nei.bid == nei.bid || util.Vec2.dist(nei_pos, nei_nei_pos) < 0.1)
                        return;

                    var v_diff = util.Vec2.diff(nei_nei_pos, nei_pos);
                    var ang = Math.atan2(v_diff.y, v_diff.x) - nei_angle;

                    if (ang < 0)
                        ang += 2 * Math.PI;

                    nei_neighbours.push(ang);
                });
                nei_neighbours.sort(function (nei1, nei2)
                {
                    return nei1 - nei2;
                });

                if (nei_neighbours[0] <= Math.PI * 1.01 && nei_neighbours[nei_neighbours.length-1] <= 1.01 * Math.PI)
                    max_angle *= -1;

            }
        }

        angle = (max_angle / 2) + Math.atan2(neighbours[max_i].v.y, neighbours[max_i].v.x);

        v = v.rotate(angle);
    }

    v.add_(pos);

    var a = ui.render.findClosestAtom(v, 0.1);

    if (a == null)
        a = {label: 'C'};
    else
        a = a.id;

    return {atom: a, pos: v};
};

//
// Canvas size
//
ui.onOffsetChanged = function (newOffset, oldOffset)
{
    if (oldOffset == null)
        return;

    var delta = new util.Vec2(newOffset.x - oldOffset.x, newOffset.y - oldOffset.y);

    ui.client_area.scrollLeft += delta.x;
    ui.client_area.scrollTop += delta.y;
};

ui.updateSelection = function (selection, nodraw)
{
    selection = selection || {};
    for (var map in rnd.ReStruct.maps) {
        if (Object.isUndefined(selection[map]))
            ui.selection[map] = [];
        else
            ui.selection[map] = selection[map];
    }

    ui.selection.bonds = ui.selection.bonds.filter(function (bid)
    {
        var bond = ui.ctab.bonds.get(bid);
        return (ui.selection.atoms.indexOf(bond.begin) != -1 && ui.selection.atoms.indexOf(bond.end) != -1);
    });

    if (!nodraw) {
        ui.render.setSelection(ui.selection);
        ui.render.update();
    }

    ui.updateClipboardButtons();
};

ui.selected = function ()
{
    for (var map in rnd.ReStruct.maps) {
        if (!Object.isUndefined(ui.selection[map]) && ui.selection[map].length > 0) {
            return true;
        }
    }
    return false;
};

ui.selectedAtom = function ()
{
	return !Object.isUndefined(ui.selection.atoms) && ui.selection.atoms.length > 0;
};

ui.selectAll = function ()
{
    // TODO cleanup
/*
    var mode = ui.modeType();
    if (mode == ui.MODE.ERASE || mode == ui.MODE.SGROUP)
        ui.selectMode(ui.defaultSelector);

    var selection = {};
    for (var map in rnd.ReStruct.maps) {
        selection[map] = ui.ctab[map].ikeys();
    }

    ui.updateSelection(selection);
*/
    if (!ui.ctab.isBlank()) {
        ui.selectMode(p$('selector').getAttribute('selid'));
        ui.editor.selectAll();
    }
};

ui.removeSelected = function ()
{
    ui.addUndoAction(ui.Action.fromFragmentDeletion());
    for (var map in rnd.ReStruct.maps)
        ui.selection[map] = [];
    ui.render.update();
    ui.updateClipboardButtons();
};

ui.hideBlurredControls = function ()
{
    var ret = false;
    [
        'input_label',
        'selector_dropdown_list',
        'bond_dropdown_list',
        'template_dropdown_list',
        'reaction_dropdown_list',
        'rgroup_dropdown_list'
    ].each(
        function(el) { el = p$(el); if (el.visible()) { el.hide(); ret = true; }}
    );
    return ret;
};

ui.onMouseDown_Ketcher = function (event)
{
    ui.hideBlurredControls();
    //util.stopEventPropagation(event);
};

ui.onMouseUp_Ketcher = function (event)
{
    util.stopEventPropagation(event);
};

//
// Atom attachment points dialog
//
ui.showAtomAttachmentPoints = function(params)
{
    p$('atom_ap1').checked = ((params.selection || 0) & 1) > 0;
    p$('atom_ap2').checked = ((params.selection || 0) & 2) > 0;
    ui.showDialog('atom_attpoints');
    var _onOk = new Event.Handler('atom_attpoints_ok', 'click', undefined, function() {
        ui.hideDialog('atom_attpoints');
        if ('onOk' in params) params['onOk']((p$('atom_ap1').checked ? 1 : 0) + (p$('atom_ap2').checked ? 2 : 0));
        _onOk.stop();
    }).start();
    var _onCancel = new Event.Handler('atom_attpoints_cancel', 'click', undefined, function() {
        ui.hideDialog('atom_attpoints');
        if ('onCancel' in params) params['onCancel']();
        _onCancel.stop();
    }).start();
    p$('atom_attpoints_ok').focus();
};

//
// Atom properties dialog
//
ui.showAtomProperties = function (id)
{
    p$('atom_properties').atom_id = id;
    p$('atom_label').value = ui.render.atomGetAttr(id, 'label');
    ui.onChange_AtomLabel.call(p$('atom_label'));
    var value = ui.render.atomGetAttr(id, 'charge');
    p$('atom_charge').value = (value == 0 ? '' : value);
    value = ui.render.atomGetAttr(id, 'isotope');
    p$('atom_isotope').value = (value == 0 ? '' : value);
    p$('atom_valence').value = (!ui.render.atomGetAttr(id, 'explicitValence') ? '' : ui.render.atomGetAttr(id, 'valence'));
    p$('atom_radical').value = ui.render.atomGetAttr(id, 'radical');

    p$('atom_inversion').value = ui.render.atomGetAttr(id, 'invRet');
    p$('atom_exactchange').value = ui.render.atomGetAttr(id, 'exactChangeFlag') ? 1 : 0;
    p$('atom_ringcount').value = ui.render.atomGetAttr(id, 'ringBondCount');
    p$('atom_substitution').value = ui.render.atomGetAttr(id, 'substitutionCount');
    p$('atom_unsaturation').value = ui.render.atomGetAttr(id, 'unsaturatedAtom');
    p$('atom_hcount').value = ui.render.atomGetAttr(id, 'hCount');

    ui.showDialog('atom_properties');
    p$('atom_label').activate();
};

ui.applyAtomProperties = function ()
{
    ui.hideDialog('atom_properties');

    var id = p$('atom_properties').atom_id;

    ui.addUndoAction(ui.Action.fromAtomAttrs(id,
    {
        label: p$('atom_label').value,
        charge: p$('atom_charge').value == '' ? 0 : parseInt(p$('atom_charge').value),
        isotope: p$('atom_isotope').value == '' ? 0 : parseInt(p$('atom_isotope').value),
        explicitValence: p$('atom_valence').value != '',
        valence: p$('atom_valence').value == '' ? ui.render.atomGetAttr(id, 'valence') : parseInt(p$('atom_valence').value),
        radical: parseInt(p$('atom_radical').value),
        // reaction flags
        invRet: parseInt(p$('atom_inversion').value),
        exactChangeFlag: parseInt(p$('atom_exactchange').value) ? true : false,
        // query flags
        ringBondCount: parseInt(p$('atom_ringcount').value),
        substitutionCount: parseInt(p$('atom_substitution').value),
        unsaturatedAtom: parseInt(p$('atom_unsaturation').value),
        hCount: parseInt(p$('atom_hcount').value)
    }), true);

    ui.render.update();
};

ui.onChange_AtomLabel = function ()
{
    this.value = this.value.strip().capitalize();

    var element = chem.Element.getElementByLabel(this.value);

    if (element == null && this.value != 'A' && this.value != '*' && this.value != 'Q' && this.value != 'X' && this.value != 'R')
    {
        this.value = ui.render.atomGetAttr(p$('atom_properties').atom_id, 'label');

        if (this.value != 'A' && this.value != '*')
            element = chem.Element.getElementByLabel(this.value);
    }

    if (this.value == 'A' || this.value == '*')
        p$('atom_number').value = "any";
    else if (!element)
        p$('atom_number').value = "";
    else
        p$('atom_number').value = element.toString();
};

ui.onChange_AtomCharge = function ()
{
    if (this.value.strip() == '' || this.value == '0')
        this.value = '';
    else if (!this.value.match(/^[+-]?[1-9][0-9]{0,1}$/))
        this.value = ui.render.atomGetAttr(p$('atom_properties').atom_id, 'charge');
};

ui.onChange_AtomIsotope = function ()
{
    if (this.value == util.getElementTextContent(p$('atom_number')) || this.value.strip() == '' || this.value == '0')
        this.value = '';
    else if (!this.value.match(/^[1-9][0-9]{0,2}$/))
        this.value = ui.render.atomGetAttr(p$('atom_properties').atom_id, 'isotope');
};

ui.onChange_AtomValence = function ()
{
    /*
    if (this.value.strip() == '')
        this.value = '';
    else if (!this.value.match(/^[0-9]$/))
        this.value = ui.render.atomGetAttr(p$('atom_properties').atom_id, 'valence');
    */
};

//
// Bond properties dialog
//
ui.showBondProperties = function (id)
{
    p$('bond_properties').bond_id = id;

    var type = ui.render.bondGetAttr(id, 'type');
    var stereo = ui.render.bondGetAttr(id, 'stereo');

    for (var bond in ui.bondTypeMap)
    {
        if (ui.bondTypeMap[bond].type == type && ui.bondTypeMap[bond].stereo == stereo)
            break;
    }

    p$('bond_type').value = bond;
    p$('bond_topology').value = ui.render.bondGetAttr(id, 'topology') || 0;
    p$('bond_center').value = ui.render.bondGetAttr(id, 'reactingCenterStatus') || 0;

    ui.showDialog('bond_properties');
    p$('bond_type').activate();
};

ui.applyBondProperties = function ()
{
    ui.hideDialog('bond_properties');

    var id = p$('bond_properties').bond_id;
    var bond = Object.clone(ui.bondTypeMap[p$('bond_type').value]);

    bond.topology = parseInt(p$('bond_topology').value);
    bond.reactingCenterStatus = parseInt(p$('bond_center').value);

    ui.addUndoAction(ui.Action.fromBondAttrs(id, bond), true);

    ui.render.update();
};

//
// S-Group properties
//
ui.showSGroupProperties = function (id, tool, selection, onOk, onCancel)
{
    if (!tool) {
        throw new Error("Tool not specified. Note: this method should only be invoked by rnd.Editor.SGroupTool.SGroupHelper, all other usages are obsolete.");
    }
    if (p$('sgroup_properties').visible())
        return;

    var type = (id == null) ? 'GEN' : ui.render.sGroupGetType(id);

    p$('sgroup_properties').sgroup_id = id;
    p$('sgroup_type').value = type;
    ui.onChange_SGroupType.call(p$('sgroup_type'));

    switch (type)
    {
    case 'SRU':
        p$('sgroup_connection').value = ui.render.sGroupGetAttr(id, 'connectivity');
        p$('sgroup_label').value = ui.render.sGroupGetAttr(id, 'subscript');
        break;
    case 'MUL':
        p$('sgroup_label').value = ui.render.sGroupGetAttr(id, 'mul');
        break;
    case 'SUP':
        p$('sgroup_label').value = ui.render.sGroupGetAttr(id, 'name');
        break;
    case 'DAT':
        p$('sgroup_field_name').value = ui.render.sGroupGetAttr(id, 'fieldName');
        p$('sgroup_field_value').value = ui.render.sGroupGetAttr(id, 'fieldValue');
        var isAttached = ui.render.sGroupGetAttr(id, 'attached');
        var isAbsolute = ui.render.sGroupGetAttr(id, 'absolute');
        (isAttached ? p$('sgroup_pos_attached') : (isAbsolute ? p$('sgroup_pos_absolute') : p$('sgroup_pos_relative'))).checked = true;
        break;
    }

    if (type != 'DAT')
    {
        p$('sgroup_field_name').value = '';
        p$('sgroup_field_value').value = '';
    }

    var onClickCancel = function ()
    {
        ui.hideDialog('sgroup_properties');
        resetListeners();
        onCancel.call(tool);
    };

    var onClickOk = function ()
    {
        ui.hideDialog('sgroup_properties');
        var id = p$('sgroup_properties').sgroup_id;

        var type = p$('sgroup_type').value;
        var attrs =
        {
            mul: null,
            connectivity: '',
            name: '',
            subscript: '',
            fieldName: '',
            fieldValue: ''
        };

        switch (type)
        {
        case 'SRU':
            attrs.connectivity = p$('sgroup_connection').value;
            attrs.subscript = p$('sgroup_label').value;
            break;
        case 'MUL':
            attrs.mul = parseInt(p$('sgroup_label').value);
            break;
        case 'SUP':
            attrs.name = p$('sgroup_label').value;
            break;
        case 'DAT':
            attrs.fieldName = p$('sgroup_field_name').value.strip();
            attrs.fieldValue = p$('sgroup_field_value').value.strip();
            attrs.absolute = p$('sgroup_pos_absolute').checked;
            attrs.attached = p$('sgroup_pos_attached').checked;

            if (attrs.fieldName == '' || attrs.fieldValue == '')
            {
                alert("Please, specify data field name and value.");
                ui.showDialog('sgroup_properties');
                return;
            }
            break;
        }

        resetListeners();
        onOk.call(tool, id, type, attrs);
    };

    var resetListeners = function () {
        p$('sgroup_prop_cancel').stopObserving('click', onClickCancel);
        p$('sgroup_prop_ok').stopObserving('click', onClickOk);
    };

    p$('sgroup_prop_cancel').observe('click', onClickCancel);
    p$('sgroup_prop_ok').observe('click', onClickOk);

    ui.showDialog('sgroup_properties');
    ui.sGroupDlgSelection = selection;
    p$('sgroup_type').activate();
};

ui.onChange_SGroupLabel = function ()
{
    if (p$('sgroup_type').value == 'MUL' && !this.value.match(/^[1-9][0-9]{0,2}$/))
        this.value = '1';
};

ui.onChange_SGroupType = function ()
{
    var type = p$('sgroup_type').value;

    if (type == 'DAT')
    {
        $$('.generalSGroup').each(function (el) {el.hide()});
        $$('.dataSGroup').each(function (el) {el.show()});

        p$('sgroup_field_name').activate();

        return;
    }

    $$('.generalSGroup').each(function (el) {el.show()});
    $$('.dataSGroup').each(function (el) {el.hide()});

    p$('sgroup_label').disabled = (type != 'SRU') && (type != 'MUL') && (type != 'SUP');
    p$('sgroup_connection').disabled = (type != 'SRU');

    if (type == 'MUL' && !p$('sgroup_label').value.match(/^[1-9][0-9]{0,2}$/))
        p$('sgroup_label').value = '1';
    else if (type == 'SRU')
        p$('sgroup_label').value = 'n';
    else if (type == 'GEN' || type == 'SUP')
        p$('sgroup_label').value = '';

    if (type != 'GEN')
        p$('sgroup_label').activate();
};

//
// Reaction auto-mapping
//

ui.showAutomapProperties = function(params)
{
    ui.showDialog('automap_properties');

    var _onOk = new Event.Handler('automap_ok', 'click', undefined, function() {
        _onOk.stop();
        _onCancel.stop();
        if (params && 'onOk' in params) params['onOk'](p$('automap_mode').value);
        ui.hideDialog('automap_properties');
    }).start();
    var _onCancel = new Event.Handler('automap_cancel', 'click', undefined, function() {
        _onOk.stop();
        _onCancel.stop();
        ui.hideDialog('automap_properties');
        if (params && 'onCancel' in params) params['onCancel']();
    }).start();

    p$('automap_mode').activate();
};

//
// Element table
//

ui.onClick_ElemTableButton = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;
    ui.showElemTable();
};

ui.showElemTable = function ()
{
    if (p$('elem_table').visible())
        return;

    ui.showDialog('elem_table');
    if (typeof(ui.elem_table_obj) == 'undefined') {
        ui.elem_table_obj = new rnd.ElementTable('elem_table_area', {
            'fillColor':'#DADADA',
            'fillColorSelected':'#FFFFFF',
            'frameColor':'#E8E8E8',
            'fontSize':23,
            'buttonHalfSize':18
        }, true);
        ui.elem_table_area = ui.elem_table_obj.renderTable();
        p$('elem_table_single').checked = true;
    }
    ui.elem_table_obj.store();
    p$('elem_table_ok').focus();
};


ui.showRGroupTable = function(params)
{
    if (!p$('rgroup_table').visible()) {
        params = params || {};
        ui.showDialog('rgroup_table');
        if (typeof(ui.rgroup_table_obj) == 'undefined') {
            ui.rgroup_table_obj = new rnd.RGroupTable('rgroup_table_area', {
                'fillColor':'#DADADA',
                'fillColorSelected':'#FFFFFF',
                'frameColor':'#E8E8E8',
                'fontSize':18,
                'buttonHalfSize':18
            }, true);
        }
        ui.rgroup_table_obj.setMode(params.mode || 'multiple');
        ui.rgroup_table_obj.setSelection(params.selection || 0);
        var _onOk = new Event.Handler('rgroup_table_ok', 'click', undefined, function() {
            _onOk.stop();
            _onCancel.stop();
            ui.hideDialog('rgroup_table');
            if ('onOk' in params) params['onOk'](ui.rgroup_table_obj.selection);
        }).start();
        var _onCancel = new Event.Handler('rgroup_table_cancel', 'click', undefined, function() {
            _onOk.stop();
            _onCancel.stop();
            ui.hideDialog('rgroup_table');
            if ('onCancel' in params) params['onCancel']();
        }).start();
        p$('rgroup_table_ok').focus();
    }
};

ui.showRLogicTable = function(params)
{
    params = params || {};
    params.rlogic = params.rlogic || {};
    p$('rlogic_occurrence').value = params.rlogic.occurrence || '>0';
    p$('rlogic_resth').value = params.rlogic.resth || '0';
    p$('rlogic_if').innerHTML = '<option value="0">Always</option>';
    for (var r = 1; r <= 32; r++) if (r != params.rgid && 0 != (params.rgmask & (1 << (r - 1)))) {
        p$('rlogic_if').innerHTML += '<option value="' + r + '">IF R' + params.rgid + ' THEN R' + r + '</option>';
    }
    p$('rlogic_if').value = params.rlogic.ifthen;
    ui.showDialog('rlogic_table');

    var _onOk = new Event.Handler('rlogic_ok', 'click', undefined, function() {
        _onOk.stop();
        _onCancel.stop();
        ui.hideDialog('rlogic_table');
        if (params && 'onOk' in params) params['onOk']({
            'occurrence' : p$('rlogic_occurrence').value,
            'resth' : p$('rlogic_resth').value == '1',
            'ifthen' : parseInt(p$('rlogic_if').value)
        });
    }).start();
    var _onCancel = new Event.Handler('rlogic_cancel', 'click', undefined, function() {
        _onOk.stop();
        _onCancel.stop();
        ui.hideDialog('rlogic_table');
        if (params && 'onCancel' in params) params['onCancel']();
    }).start();

    p$('rlogic_occurrence').activate();
};

ui.onSelect_ElemTableNotList = function ()
{
    try {
        ui.elem_table_obj.updateAtomProps();
    } catch(e) {
        ErrorHandler.handleError(e);
    }
};

//
// Clipboard actions
//

ui.clipboard = null;

ui.isClipboardEmpty = function ()
{
    return ui.clipboard == null;
};

ui.updateClipboardButtons = function ()
{
    if (ui.isClipboardEmpty())
        p$('paste').addClassName('buttonDisabled');
    else
        p$('paste').removeClassName('buttonDisabled');

    if (ui.selected())
    {
        p$('copy').removeClassName('buttonDisabled');
        p$('cut').removeClassName('buttonDisabled');
    } else
    {
        p$('copy').addClassName('buttonDisabled');
        p$('cut').addClassName('buttonDisabled');
    }
};

ui.copy = function (struct, selection)
{
    if (!struct) {
        struct = ui.ctab;
        selection = ui.selection;
    }
    ui.clipboard =
    {
        atoms: new Array(),
        bonds: new Array(),
        sgroups: new Array(),
        rxnArrows: new Array(),
        rxnPluses: new Array(),
        chiralFlags: new Array(),
        rgmap: {},
        rgroups: {},
        // RB: let it be here for the moment
        // TODO: "clipboard" support to be moved to editor module
        getAnchorPosition: function() {
            if (this.atoms.length) {
                return this.atoms[0].pp; // TODO: check
            } else if (this.rxnArrows.length) {
                return this.rxnArrows[0].pp;
            } else if (this.rxnPluses.length) {
                return this.rxnPluses[0].pp;
            } else if (this.chiralFlags.length) {
                return this.chiralFlags[0].pp;
            }
        }
    };

    ui.structToClipboard(ui.clipboard, struct, selection);
};

ui.structToClipboard = function (clipboard, struct, selection)
    {
    selection = selection || {
        atoms: struct.atoms.keys(),
        bonds: struct.bonds.keys(),
        rxnArrows: struct.rxnArrows.keys(),
        rxnPluses: struct.rxnPluses.keys()
    };

    var mapping = {};

    selection.atoms.each(function (id)
    {
        var new_atom = new chem.Struct.Atom(struct.atoms.get(id));
        new_atom.pos = new_atom.pp;
        mapping[id] = clipboard.atoms.push(new chem.Struct.Atom(new_atom)) - 1;
    });

    selection.bonds.each(function (id)
    {
        var new_bond = new chem.Struct.Bond(struct.bonds.get(id));
        new_bond.begin = mapping[new_bond.begin];
        new_bond.end = mapping[new_bond.end];
        clipboard.bonds.push(new chem.Struct.Bond(new_bond));
    });

    var sgroup_counts = new Hash();

    // determine selected sgroups
    selection.atoms.each(function (id)
    {
        var sg = util.Set.list(struct.atoms.get(id).sgs);

        sg.each(function (sid)
        {
            var n = sgroup_counts.get(sid);
            if (Object.isUndefined(n))
                n = 1;
            else
                n++;
            sgroup_counts.set(sid, n);
        }, this);
    }, this);

    sgroup_counts.each(function (sg)
    {
        var sid = parseInt(sg.key);

        var sgroup = struct.sgroups.get(sid);
        var sgAtoms = chem.SGroup.getAtoms(struct, sgroup);
        if (sg.value == sgAtoms.length)
        {
            var sgroup_info =
            {
                type: sgroup.type,
                attrs: sgroup.getAttrs(),
                atoms: util.array(sgAtoms)
            };

            for (var i = 0; i < sgroup_info.atoms.length; i++)
            {
                sgroup_info.atoms[i] = mapping[sgroup_info.atoms[i]];
            }

            clipboard.sgroups.push(sgroup_info);
        }
    });

    selection.rxnArrows.each(function (id)
    {
        var arrow = new chem.Struct.RxnArrow(struct.rxnArrows.get(id));
        arrow.pos = arrow.pp;
        clipboard.rxnArrows.push(arrow);
    });

    selection.rxnPluses.each(function (id)
    {
        var plus = new chem.Struct.RxnPlus(struct.rxnPluses.get(id));
        plus.pos = plus.pp;
        clipboard.rxnPluses.push(plus);
    });

    // r-groups
    var atomFragments = {};
    var fragments = util.Set.empty();
    selection.atoms.each(function (id) {
        var atom = struct.atoms.get(id);
        var frag = atom.fragment;
        atomFragments[id] = frag;
        util.Set.add(fragments, frag);
    });

    var rgids = util.Set.empty();
    util.Set.each(fragments, function(frid){
        var atoms = chem.Struct.Fragment.getAtoms(struct, frid);
        for (var i = 0; i < atoms.length; ++i)
            if (!util.Set.contains(atomFragments, atoms[i]))
                return;
        var rgid = chem.Struct.RGroup.findRGroupByFragment(struct.rgroups, frid);
        clipboard.rgmap[frid] = rgid;
        util.Set.add(rgids, rgid);
    }, this);
    
    util.Set.each(rgids, function(id){
        clipboard.rgroups[id] = struct.rgroups.get(id).getAttrs();
    }, this);
};

ui.onClick_Cut = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    ui.copy();
    ui.removeSelected();
};

ui.onClick_Copy = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    ui.copy();
    ui.updateSelection();
};

ui.onClick_Paste = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    ui.selectMode('paste');
};

ui.onClick_Undo = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    ui.undo();
};

ui.onClick_Redo = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;

    ui.redo();
};

ui.showLabelEditor = function(aid)
{
    // TODO: RB: to be refactored later, need to attach/detach listeners here as anon-functions, not on global scope (ui.onKeyPress_InputLabel, onBlur, etc)
    var input_el = p$('input_label');

    var offset = Math.min(6 * ui.zoom, 16);

    input_el.atom_id = aid;
    input_el.value = ui.render.atomGetAttr(aid, 'label');
    input_el.style.fontSize = (offset * 2).toString() + 'px';

    input_el.show();

    var atom_pos = ui.render.obj2view(ui.render.atomGetPos(aid));
    var offset_client = ui.client_area.cumulativeOffset();
    var offset_parent = Element.cumulativeOffset(input_el.offsetParent);
    var d = 0; // TODO: fix/Math.ceil(4 * ui.abl() / 100);
    input_el.style.left = (atom_pos.x + offset_client.left - offset_parent.left - offset - d).toString() + 'px';
    input_el.style.top = (atom_pos.y + offset_client.top - offset_parent.top - offset - d).toString() + 'px';

    input_el.activate();
};
/****************************************************************************
 * Copyright (C) 2009-2011 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

if (typeof(ui) == 'undefined')
    ui = function () {};

//
// Undo/redo actions
//
ui.Action = function ()
{
    this.operations = new Array();
};

ui.Action.prototype.addOp = function(op) {
    this.operations.push(op);
    return op;
};

ui.Action.prototype.mergeWith = function (action)
{
    this.operations = this.operations.concat(action.operations);
    return this; //rbalabanov: let it be possible to chain actions in code easier
};

// Perform action and return inverted one
ui.Action.prototype.perform = function ()
{
    var action = new ui.Action();
    var idx = 0;

    this.operations.each(function (op) {
        action.addOp(op.perform(ui.editor));
        idx++;
    }, this);

    action.operations.reverse();
    return action;
};

ui.Action.prototype.isDummy = function ()
{
    return this.operations.detect(function(op) {
        return !op.isDummy(ui.editor); // TODO [RB] the condition is always true for ui.Action.Op* operations
    }, this) == null;
};

ui.Action.fromSelectedAtomsMove = function(selection, d)
{
    selection = selection || ui.selection;

    var action = new ui.Action();

    selection.atoms.each(function(id) {
        action.addOp(new ui.Action.OpAtomMove(id, d));
    }, this);

    return action;
};

ui.Action.fromMultipleMove = function (lists, d)
{
    d = new util.Vec2(d);

    var action = new ui.Action();
    var i;
    if (lists.atoms)
        for (i = 0; i < lists.atoms.length; ++i)
            action.addOp(new ui.Action.OpAtomMove(lists.atoms[i], d));

    if (lists.rxnArrows)
        for (i = 0; i < lists.rxnArrows.length; ++i)
            action.addOp(new ui.Action.OpRxnArrowMove(lists.rxnArrows[i], d));

    if (lists.rxnPluses)
        for (i = 0; i < lists.rxnPluses.length; ++i)
            action.addOp(new ui.Action.OpRxnPlusMove(lists.rxnPluses[i], d));

    if (lists.sgroupData)
        for (i = 0; i < lists.sgroupData.length; ++i)
            action.addOp(new ui.Action.OpSGroupDataMove(lists.sgroupData[i], d));
    
    if (lists.chiralFlags)
        for (i = 0; i < lists.chiralFlags.length; ++i)
            action.addOp(new ui.Action.OpChiralFlagMove(d));

    return action.perform();
};

ui.Action.fromAtomAttrs = function (id, attrs)
{
    var action = new ui.Action();
    new Hash(attrs).each(function (attr) {
        action.addOp(new ui.Action.OpAtomAttr(id, attr.key, attr.value));
    }, this);
    return action.perform();
};

ui.Action.fromSelectedAtomsAttrs = function (attrs)
{
    var action = new ui.Action();
    new Hash(attrs).each(function(attr) {
        ui.selection.atoms.each(function(id) {
            action.addOp(new ui.Action.OpAtomAttr(id, attr.key, attr.value));
        }, this)
    }, this);
    return action.perform();
};

ui.Action.fromBondAttrs = function (id, attrs, flip)
{
    var action = new ui.Action();

    new Hash(attrs).each(function(attr) {
        action.addOp(new ui.Action.OpBondAttr(id, attr.key, attr.value));
    }, this);
    if (flip) {
        action.mergeWith(ui.Action.toBondFlipping(id));
    }
    return action.perform();
};

ui.Action.fromSelectedBondsAttrs = function (attrs, flips)
{
    var action = new ui.Action();

    attrs = new Hash(attrs);

    ui.selection.bonds.each(function(id) {
        attrs.each(function(attr) {
            action.addOp(new ui.Action.OpBondAttr(id, attr.key, attr.value));
        }, this);
    }, this);
    if (flips)
        flips.each(function (id) {
            action.mergeWith(ui.Action.toBondFlipping(id));
        }, this);
    return action.perform();
};

ui.Action.fromAtomAddition = function (pos, atom)
{
    atom = Object.clone(atom);
    var action = new ui.Action();
    atom.fragment = action.addOp(new ui.Action.OpFragmentAdd().perform(ui.editor)).frid;
    action.addOp(new ui.Action.OpAtomAdd(atom, pos).perform(ui.editor));
    return action;
};

ui.Action.mergeFragments = function (action, frid, frid2) {
    if (frid2 != frid && Object.isNumber(frid2)) {
        var rgid = chem.Struct.RGroup.findRGroupByFragment(ui.render.ctab.molecule.rgroups, frid2);
        if (!Object.isUndefined(rgid)) {
            action.mergeWith(ui.Action.fromRGroupFragment(null, frid2));
        }
        ui.render.ctab.molecule.atoms.each(function(aid, atom) {
            if (atom.fragment == frid2) {
                action.addOp(new ui.Action.OpAtomAttr(aid, 'fragment', frid).perform(ui.editor));
            }
        });
        action.addOp(new ui.Action.OpFragmentDelete(frid2).perform(ui.editor));
    }
};

ui.Action.fromBondAddition = function (bond, begin, end, pos, pos2)
{
    var action = new ui.Action();

    var frid = null;
    if (!Object.isNumber(begin)) {
        if (Object.isNumber(end)) {
            frid = ui.render.atomGetAttr(end, 'fragment');
        }
    }
    else {
        frid = ui.render.atomGetAttr(begin, 'fragment');
        if (Object.isNumber(end)) {
            var frid2 = ui.render.atomGetAttr(end, 'fragment');
            ui.Action.mergeFragments(action, frid, frid2);
        }
    }
    if (frid == null) {
        frid = action.addOp(new ui.Action.OpFragmentAdd().perform(ui.editor)).frid;
    }

    if (!Object.isNumber(begin)) {
        begin.fragment = frid;
        begin = action.addOp(new ui.Action.OpAtomAdd(begin, pos).perform(ui.editor)).data.aid;

        pos = pos2;
    }
    else {
        if (ui.render.atomGetAttr(begin, 'label') == '*') {
            action.addOp(new ui.Action.OpAtomAttr(begin, 'label', 'C').perform(ui.editor));
        }
    }


    if (!Object.isNumber(end)) {
        end.fragment = frid;
        // TODO: <op>.data.aid here is a hack, need a better way to access the id of a newly created atom
        end = action.addOp(new ui.Action.OpAtomAdd(end, pos).perform(ui.editor)).data.aid;
        if (Object.isNumber(begin)) {
            ui.render.atomGetSGroups(begin).each(function (sid) {
                action.addOp(new ui.Action.OpSGroupAtomAdd(sid, end).perform(ui.editor));
            }, this);
        }
    }
    else {
        if (ui.render.atomGetAttr(end, 'label') == '*') {
            action.addOp(new ui.Action.OpAtomAttr(end, 'label', 'C').perform(ui.editor));
        }
    }

    action.addOp(new ui.Action.OpBondAdd(begin, end, bond).perform(ui.editor));

    action.operations.reverse();

    return [action, begin, end];
};

ui.Action.fromArrowAddition = function (pos)
{
    var action = new ui.Action();
    if (ui.ctab.rxnArrows.count() < 1) {
        action.addOp(new ui.Action.OpRxnArrowAdd(pos).perform(ui.editor));
    }
    return action;
};

ui.Action.fromArrowDeletion = function (id)
{
    var action = new ui.Action();
    action.addOp(new ui.Action.OpRxnArrowDelete(id));
    return action.perform();
};

ui.Action.fromChiralFlagAddition = function (pos)
{
    var action = new ui.Action();
    if (ui.render.ctab.chiralFlags.count() < 1) {
        action.addOp(new ui.Action.OpChiralFlagAdd(pos).perform(ui.editor));
    }
    return action;
};

ui.Action.fromChiralFlagDeletion = function ()
{
    var action = new ui.Action();
    action.addOp(new ui.Action.OpChiralFlagDelete());
    return action.perform();
};

ui.Action.fromPlusAddition = function (pos)
{
    var action = new ui.Action();
    action.addOp(new ui.Action.OpRxnPlusAdd(pos).perform(ui.editor));
    return action;
};

ui.Action.fromPlusDeletion = function (id)
{
    var action = new ui.Action();
    action.addOp(new ui.Action.OpRxnPlusDelete(id));
    return action.perform();
};

// Add action operation to remove atom from s-group if needed
ui.Action.prototype.removeAtomFromSgroupIfNeeded = function (id)
{
    var sgroups = ui.render.atomGetSGroups(id);

    if (sgroups.length > 0)
    {
        sgroups.each(function (sid)
        {
            this.addOp(new ui.Action.OpSGroupAtomRemove(sid, id));
        }, this);

        return true;
    }

    return false;
};

// Add action operations to remove whole s-group if needed
ui.Action.prototype.removeSgroupIfNeeded = function (atoms)
{
    var R = ui.render;
    var RS = R.ctab;
    var DS = RS.molecule;
    var sg_counts = new Hash();

    atoms.each(function (id)
    {
        var sgroups = ui.render.atomGetSGroups(id);

        sgroups.each(function (sid)
        {
            var n = sg_counts.get(sid);
            if (Object.isUndefined(n))
                n = 1;
            else
                n++;
            sg_counts.set(sid, n);
        }, this);
    }, this);

    sg_counts.each(function (sg)
    {
        var sid = parseInt(sg.key);
        var sg_atoms = ui.render.sGroupGetAtoms(sid);

        if (sg_atoms.length == sg.value)
        { // delete whole s-group
            var sgroup = DS.sgroups.get(sid);
            this.mergeWith(ui.Action.sGroupAttributeAction(sid, sgroup.getAttrs()));
            this.addOp(new ui.Action.OpSGroupDelete(sid));
        }
    }, this);
};

ui.Action.fromAtomDeletion = function (id)
{
    var action = new ui.Action();
    var atoms_to_remove = new Array();

    var frid = ui.ctab.atoms.get(id).fragment;

    ui.render.atomGetNeighbors(id).each(function (nei)
    {
        action.addOp(new ui.Action.OpBondDelete(nei.bid));// [RB] !!
        if (ui.render.atomGetDegree(nei.aid) == 1)
        {
            if (action.removeAtomFromSgroupIfNeeded(nei.aid))
                atoms_to_remove.push(nei.aid);

            action.addOp(new ui.Action.OpAtomDelete(nei.aid));
        }
    }, this);

    if (action.removeAtomFromSgroupIfNeeded(id))
        atoms_to_remove.push(id);

    action.addOp(new ui.Action.OpAtomDelete(id));

    action.removeSgroupIfNeeded(atoms_to_remove);

    action = action.perform();

    action.mergeWith(ui.Action.__fromFragmentSplit(frid));

    return action;
};

ui.Action.fromBondDeletion = function (id)
{
    var action = new ui.Action();
    var bond = ui.ctab.bonds.get(id);
    var frid = ui.ctab.atoms.get(bond.begin).fragment;
    var atoms_to_remove = new Array();

    action.addOp(new ui.Action.OpBondDelete(id));

    if (ui.render.atomGetDegree(bond.begin) == 1)
    {
        if (action.removeAtomFromSgroupIfNeeded(bond.begin))
            atoms_to_remove.push(bond.begin);

        action.addOp(new ui.Action.OpAtomDelete(bond.begin));
    }

    if (ui.render.atomGetDegree(bond.end) == 1)
    {
        if (action.removeAtomFromSgroupIfNeeded(bond.end))
            atoms_to_remove.push(bond.end);

        action.addOp(new ui.Action.OpAtomDelete(bond.end));
    }

    action.removeSgroupIfNeeded(atoms_to_remove);

    action = action.perform();

    action.mergeWith(ui.Action.__fromFragmentSplit(frid));

    return action;
};

ui.Action.__fromFragmentSplit = function(frid) { // TODO [RB] the thing is too tricky :) need something else in future
    var action = new ui.Action();
    var rgid = chem.Struct.RGroup.findRGroupByFragment(ui.ctab.rgroups, frid);
    ui.ctab.atoms.each(function(aid, atom) {
        if (atom.fragment == frid) {
            var newfrid = action.addOp(new ui.Action.OpFragmentAdd().perform(ui.editor)).frid;
            var processAtom = function(aid1) {
                action.addOp(new ui.Action.OpAtomAttr(aid1, 'fragment', newfrid).perform(ui.editor));
                ui.render.atomGetNeighbors(aid1).each(function(nei) {
                    if (ui.ctab.atoms.get(nei.aid).fragment == frid) {
                        processAtom(nei.aid);
                    }
                });
            };
            processAtom(aid);
            if (rgid) {
                action.mergeWith(ui.Action.fromRGroupFragment(rgid, newfrid));
            }
        }
    });
    if (frid != -1) {
        action.mergeWith(ui.Action.fromRGroupFragment(0, frid));
        action.addOp(new ui.Action.OpFragmentDelete(frid).perform(ui.editor));
    }
    return action;
};

ui.Action.fromFragmentAddition = function (atoms, bonds, sgroups, rxnArrows, rxnPluses)
{
    var action = new ui.Action();

    /*
    atoms.each(function (aid)
    {
        ui.render.atomGetNeighbors(aid).each(function (nei)
        {
            if (ui.selection.bonds.indexOf(nei.bid) == -1)
                ui.selection.bonds = ui.selection.bonds.concat([nei.bid]);
        }, this);
    }, this);
    */

    // TODO: merge close atoms and bonds

    sgroups.each(function (sid)
    {
        action.addOp(new ui.Action.OpSGroupDelete(sid));
    }, this);


    bonds.each(function (bid) {
        action.addOp(new ui.Action.OpBondDelete(bid));
    }, this);


    atoms.each(function(aid) {
        action.addOp(new ui.Action.OpAtomDelete(aid));
    }, this);

    rxnArrows.each(function (id) {
        action.addOp(new ui.Action.OpRxnArrowDelete(id));
    }, this);

    rxnPluses.each(function (id) {
        action.addOp(new ui.Action.OpRxnPlusDelete(id));
    }, this);

    action.mergeWith(new ui.Action.__fromFragmentSplit(-1));

    return action;
};

ui.Action.fromFragmentDeletion = function(selection)
{
    selection = selection || ui.selection;

    var action = new ui.Action();
    var atoms_to_remove = new Array();

    var frids = [];

    var actionRemoveDataSGroups = new ui.Action();
    if (selection.sgroupData) {
        selection.sgroupData.each(function (id) {
            actionRemoveDataSGroups.mergeWith(ui.Action.fromSgroupDeletion(id));
        }, this);
    }

    selection.atoms.each(function (aid)
    {
        ui.render.atomGetNeighbors(aid).each(function (nei)
        {
            if (selection.bonds.indexOf(nei.bid) == -1)
                selection.bonds = selection.bonds.concat([nei.bid]);
        }, this);
    }, this);

    selection.bonds.each(function (bid)
    {
        action.addOp(new ui.Action.OpBondDelete(bid));

        var bond = ui.ctab.bonds.get(bid);

        if (selection.atoms.indexOf(bond.begin) == -1 && ui.render.atomGetDegree(bond.begin) == 1)
        {
            var frid1 = ui.ctab.atoms.get(bond.begin).fragment;
            if (frids.indexOf(frid1) < 0)
                frids.push(frid1);

            if (action.removeAtomFromSgroupIfNeeded(bond.begin))
                atoms_to_remove.push(bond.begin);

            action.addOp(new ui.Action.OpAtomDelete(bond.begin));
        }
        if (selection.atoms.indexOf(bond.end) == -1 && ui.render.atomGetDegree(bond.end) == 1)
        {
            var frid2 = ui.ctab.atoms.get(bond.end).fragment;
            if (frids.indexOf(frid2) < 0)
                frids.push(frid2);

            if (action.removeAtomFromSgroupIfNeeded(bond.end))
                atoms_to_remove.push(bond.end);

            action.addOp(new ui.Action.OpAtomDelete(bond.end));
        }
    }, this);


    selection.atoms.each(function (aid)
    {
        var frid3 = ui.ctab.atoms.get(aid).fragment;
        if (frids.indexOf(frid3) < 0)
            frids.push(frid3);

        if (action.removeAtomFromSgroupIfNeeded(aid))
            atoms_to_remove.push(aid);

        action.addOp(new ui.Action.OpAtomDelete(aid));
    }, this);

    action.removeSgroupIfNeeded(atoms_to_remove);

    selection.rxnArrows.each(function (id) {
        action.addOp(new ui.Action.OpRxnArrowDelete(id));
    }, this);

    selection.rxnPluses.each(function (id) {
        action.addOp(new ui.Action.OpRxnPlusDelete(id));
    }, this);

    selection.chiralFlags.each(function (id) {
        action.addOp(new ui.Action.OpChiralFlagDelete(id));
    }, this);
    
    action = action.perform();

    while (frids.length > 0) action.mergeWith(new ui.Action.__fromFragmentSplit(frids.pop()));

    action.mergeWith(actionRemoveDataSGroups);

    return action;
};

ui.Action.fromAtomMerge = function (src_id, dst_id)
{
    var fragAction = new ui.Action();
    var src_frid = ui.render.atomGetAttr(src_id, 'fragment'), dst_frid = ui.render.atomGetAttr(dst_id, 'fragment');
    if (src_frid != dst_frid) {
        ui.Action.mergeFragments(fragAction, src_frid, dst_frid);
    }

    var action = new ui.Action();

    ui.render.atomGetNeighbors(src_id).each(function (nei)
    {
        var bond = ui.ctab.bonds.get(nei.bid);
        var begin, end;

        if (bond.begin == nei.aid) {
            begin = nei.aid;
            end = dst_id;
        } else {
            begin = dst_id;
            end = nei.aid;
        }
        if (dst_id != bond.begin && dst_id != bond.end && ui.ctab.findBondId(begin, end) == -1) // TODO: improve this
        {
            action.addOp(new ui.Action.OpBondAdd(begin, end, bond));
        }
        action.addOp(new ui.Action.OpBondDelete(nei.bid));
    }, this);

    var attrs = chem.Struct.Atom.getAttrHash(ui.ctab.atoms.get(src_id));

    if (ui.render.atomGetDegree(src_id) == 1 && attrs.get('label') == '*')
        attrs.set('label', 'C');

    attrs.each(function(attr) {
        action.addOp(new ui.Action.OpAtomAttr(dst_id, attr.key, attr.value));
    }, this);

    var sg_changed = action.removeAtomFromSgroupIfNeeded(src_id);

    action.addOp(new ui.Action.OpAtomDelete(src_id));

    if (sg_changed)
        action.removeSgroupIfNeeded([src_id]);

    return action.perform().mergeWith(fragAction);
};

ui.Action.toBondFlipping = function (id)
{
    var bond = ui.ctab.bonds.get(id);

    var action = new ui.Action();
    action.addOp(new ui.Action.OpBondDelete(id));
    action.addOp(new ui.Action.OpBondAdd(bond.end, bond.begin, bond)).data.bid = id;
    return action;
};
ui.Action.fromBondFlipping = function(bid) {
    return ui.Action.toBondFlipping(bid).perform();
};

ui.Action.fromPatternOnCanvas = function (pos, pattern)
{
    var angle = 2 * Math.PI / pattern.length;
    var l = 1.0 / (2 * Math.sin(angle / 2));
    var v = new util.Vec2(0, -l);

    var action = new ui.Action();

    var fragAction = new ui.Action.OpFragmentAdd().perform(ui.editor);

    pattern.each(function() {
        action.addOp(
            new ui.Action.OpAtomAdd(
                { label: 'C', fragment: fragAction.frid },
                util.Vec2.sum(pos, v)
            ).perform(ui.editor)
        );
        v = v.rotate(angle);
    }, this);

    for (var i = 0, n = action.operations.length; i < n; i++) {
        action.addOp(
            new ui.Action.OpBondAdd(
                action.operations[i].data.aid,
                action.operations[(i + 1) % pattern.length].data.aid,
                { type: pattern[i] }
            ).perform(ui.editor)
        );
    }

    action.operations.reverse();
    action.addOp(fragAction);

    return action;
};

ui.Action.fromChain = function (p0, v, nSect, atom_id)
{
    var angle = Math.PI / 6;
    var dx = Math.cos(angle), dy = Math.sin(angle);

    var action = new ui.Action();

    var frid;
    if (atom_id != null) {
        frid = ui.render.atomGetAttr(atom_id, 'fragment');
    } else {
        frid = action.addOp(new ui.Action.OpFragmentAdd().perform(ui.editor)).frid;
    }

    var id0 = -1;
    if (atom_id != null) {
        id0 = atom_id;
    } else {
        id0 = action.addOp(new ui.Action.OpAtomAdd({ label: 'C', fragment : frid }, p0).perform(ui.editor)).data.aid;
    }

    nSect.times(function (i)
    {
        var pos = new util.Vec2(dx * (i + 1), i & 1 ? 0 : dy).rotate(v).add(p0);

        var a = ui.render.findClosestAtom(pos, 0.1);

        var id1 = -1;
        if (a == null)
        {
            id1 = action.addOp(new ui.Action.OpAtomAdd({ label: 'C', fragment : frid }, pos).perform(ui.editor)).data.aid;
        } else {
            //TODO [RB] need to merge fragments: is there a way to reuse fromBondAddition (which performs it) instead of using code below???
            id1 = a.id;
        }

        if (!ui.render.checkBondExists(id0, id1))
        {
            action.addOp(new ui.Action.OpBondAdd(id0, id1, {}).perform(ui.editor));
            var frid2 = ui.render.atomGetAttr(id1, 'fragment');
            ui.Action.mergeFragments(action, frid, frid2);
        }
        id0 = id1;
    }, this);

    action.operations.reverse();

    return action;
};

ui.Action.fromPatternOnAtom = function (aid, pattern)
{
    if (ui.render.atomGetDegree(aid) != 1)
    {
        var atom = ui.atomForNewBond(aid);
        atom.fragment = ui.render.atomGetAttr(aid, 'fragment');
        var action_res = ui.Action.fromBondAddition({type: 1}, aid, atom.atom, atom.pos);

        var action = ui.Action.fromPatternOnElement(action_res[2], pattern, true);

        action.mergeWith(action_res[0]);

        return action;
    }

    return ui.Action.fromPatternOnElement(aid, pattern, true);
};

ui.Action.fromPatternOnElement = function (id, pattern, on_atom)
{
    var angle = (pattern.length - 2) * Math.PI / (2 * pattern.length);
    var first_idx = 0; //pattern.indexOf(bond.type) + 1; // 0 if there's no
    var pos = null; // center pos
    var v = null; // rotating vector from center

    if (on_atom) {
        var nei_id = ui.render.atomGetNeighbors(id)[0].aid;
        var atom_pos = ui.render.atomGetPos(id);

        pos = util.Vec2.diff(atom_pos, ui.render.atomGetPos(nei_id));
        pos.normalize();
        pos = pos.scaled(0.5 / Math.cos(angle));
        v = pos.negated();
        pos.add_(atom_pos);
        angle = Math.PI - 2 * angle;
    }
    else {
        var bond = ui.ctab.bonds.get(id);
        var begin_pos = ui.render.atomGetPos(bond.begin);
        var end_pos = ui.render.atomGetPos(bond.end);

        v = util.Vec2.diff(end_pos, begin_pos);
        var l = v.length() / (2 * Math.cos(angle));

        v = v.scaled(l / v.length());

        var v_sym = v.rotate(-angle);
        v = v.rotate(angle);

        pos = util.Vec2.sum(begin_pos, v);
        var pos_sym = util.Vec2.sum(begin_pos, v_sym);

        var cnt = 0, bcnt = 0;
        var cnt_sym = 0, bcnt_sym = 0;

        // TODO: improve this enumeration
        ui.ctab.atoms.each(function (a_id) {
            if (util.Vec2.dist(pos, ui.render.atomGetPos(a_id)) < l * 1.1) {
                cnt++;
                bcnt += ui.render.atomGetDegree(a_id);
            }
            else if (util.Vec2.dist(pos_sym, ui.render.atomGetPos(a_id)) < l * 1.1) {
                cnt_sym++;
                bcnt_sym += ui.render.atomGetDegree(a_id);
            }
        });

        angle = Math.PI - 2 * angle;

        if (cnt > cnt_sym || (cnt == cnt_sym && bcnt > bcnt_sym)) {
            pos = pos_sym;
            v = v_sym;
        }
        else angle = -angle;

        v = v.negated();
    }

    var action = new ui.Action();
    var atom_ids = new Array(pattern.length);

    if (!on_atom) {
        atom_ids[0] = bond.begin;
        atom_ids[pattern.length - 1] = bond.end;
    }

    var frid = ui.render.ctab.molecule.atoms.get(on_atom ? id : ui.render.ctab.molecule.bonds.get(id).begin).fragment;

    (pattern.length - (on_atom ? 0 : 1)).times(function(idx) {
        if (idx > 0 || on_atom) {
            var new_pos = util.Vec2.sum(pos, v);

            var a = ui.render.findClosestAtom(new_pos, 0.1);

            if (a == null) {
                atom_ids[idx] = action.addOp(
                    new ui.Action.OpAtomAdd({ label: 'C', fragment : frid }, new_pos).perform(ui.editor)
                ).data.aid;
            }
            else {
                // TODO [RB] need to merge fragments?
                atom_ids[idx] = a.id;
            }
        }

        v = v.rotate(angle);
    }, this);

    var i = 0;

    pattern.length.times(function(idx) {
        var begin = atom_ids[idx];
        var end = atom_ids[(idx + 1) % pattern.length];
        var bond_type = pattern[(first_idx + idx) % pattern.length];

        if (!ui.render.checkBondExists(begin, end)) {
            action.addOp(new ui.Action.OpBondAdd(begin, end, { type: bond_type }).perform(ui.editor));
        }
        else {
            if (bond_type == chem.Struct.BOND.TYPE.AROMATIC) {
                var nei = ui.render.atomGetNeighbors(begin);

                nei.find(function(n) {
                    if (n.aid == end) {
                        var src_type = ui.render.bondGetAttr(n.bid, 'type');

                        if (src_type != bond_type) {
/*
                            action.addOperation(
                                ui.Action.OPERATION.BOND_ATTR,
                                { id: ui.bondMap.indexOf(n.bid), attr_name: 'type', attr_value: src_type }
                            );
                            ui.render.bondSetAttr(n.bid, 'type', bond_type);
*/
                        }
                        return true;
                    }
                    return false;
                }, this);
            }
        }

        i++;
    }, this);

    action.operations.reverse();

    return action;
};

ui.Action.fromNewCanvas = function (ctab)
{
    var action = new ui.Action();

    action.addOp(new ui.Action.OpCanvasLoad(ctab));
    return action.perform();
};

ui.Action.fromSgroupType = function (id, type)
{
    var R = ui.render;
    var cur_type = R.sGroupGetType(id);
    if (type && type != cur_type) {
        var atoms = util.array(R.sGroupGetAtoms(id));
        var attrs = R.sGroupGetAttrs(id);
        var actionDeletion = ui.Action.fromSgroupDeletion(id); // [MK] order of execution is important, first delete then recreate
        var actionAddition = ui.Action.fromSgroupAddition(type, atoms, attrs, id);
        return actionAddition.mergeWith(actionDeletion); // the actions are already performed and reversed, so we merge them backwards
    }
    return new ui.Action();
};

ui.Action.fromSgroupAttrs = function (id, attrs)
{
    var action = new ui.Action();
    var R = ui.render;
    var RS = R.ctab;
    var sg = RS.sgroups.get(id).item;

    new Hash(attrs).each(function (attr) {
        if (!sg.checkAttr(attr.key, attr.value)) {
            action.addOp(new ui.Action.OpSGroupAttr(id, attr.key, attr.value));
        }
    }, this);

    return action.perform();
};

ui.Action.sGroupAttributeAction = function (id, attrs)
{
    var action = new ui.Action();

    new Hash(attrs).each(function (attr) { // store the attribute assignment
        action.addOp(new ui.Action.OpSGroupAttr(id, attr.key, attr.value));
    }, this);

    return action;
}

ui.Action.fromSgroupDeletion = function (id)
{
    var action = new ui.Action();
    var R = ui.render;
    var RS = R.ctab;
    var DS = RS.molecule;

    if (ui.render.sGroupGetType(id) == 'SRU') {
        ui.render.sGroupsFindCrossBonds();
        var nei_atoms = ui.render.sGroupGetNeighborAtoms(id);

        nei_atoms.each(function(aid) {
            if (ui.render.atomGetAttr(aid, 'label') == '*') {
                action.addOp(new ui.Action.OpAtomAttr(aid, 'label', 'C'));
            }
        }, this);
    }

    var sg = DS.sgroups.get(id);
    var atoms = chem.SGroup.getAtoms(DS, sg);
    var attrs = sg.getAttrs();
    for (var i = 0; i < atoms.length; ++i) {
        action.addOp(new ui.Action.OpSGroupAtomRemove(id, atoms[i]));
    }
    action.addOp(new ui.Action.OpSGroupDelete(id));

    action = action.perform();

    action.mergeWith(ui.Action.sGroupAttributeAction(id, attrs));

    return action;
};

ui.Action.fromSgroupAddition = function (type, atoms, attrs, sgid)
{
    var action = new ui.Action();
    var i;

    // TODO: shoud the id be generated when OpSGroupCreate is executed?
    //      if yes, how to pass it to the following operations?
    sgid = sgid-0 === sgid ? sgid : ui.render.ctab.molecule.sgroups.newId();

    action.addOp(new ui.Action.OpSGroupCreate(sgid, type));
    for (i = 0; i < atoms.length; i++)
        action.addOp(new ui.Action.OpSGroupAtomAdd(sgid, atoms[i]));

    action = action.perform();

    if (type == 'SRU') {
        ui.render.sGroupsFindCrossBonds();
        var nei_atoms = ui.render.sGroupGetNeighborAtoms(sgid);
        var asterisk_action = new ui.Action();

        nei_atoms.each(function(aid) {
            if (ui.render.atomGetDegree(aid) == 1 && ui.render.atomIsPlainCarbon(aid)) {
                asterisk_action.addOp(new ui.Action.OpAtomAttr(aid, 'label', 'C'));
            }
        }, this);

        asterisk_action = asterisk_action.perform();
        asterisk_action.mergeWith(action);
        action = asterisk_action;
    }

    return ui.Action.fromSgroupAttrs(sgid, attrs).mergeWith(action);
};

ui.Action.fromRGroupAttrs = function(id, attrs) {
    var action = new ui.Action();
    new Hash(attrs).each(function(attr) {
        action.addOp(new ui.Action.OpRGroupAttr(id, attr.key, attr.value));
    }, this);
    return action.perform();
};

ui.Action.fromRGroupFragment = function(rgidNew, frid) {
    var action = new ui.Action();
    action.addOp(new ui.Action.OpRGroupFragment(rgidNew, frid));
    return action.perform();
};

ui.Action.fromPaste = function(objects, offset) {
    offset = offset || new util.Vec2();
    var action = new ui.Action(), amap = {}, fmap = {};
    // atoms
    for (var aid = 0; aid < objects.atoms.length; aid++) {
        var atom = Object.clone(objects.atoms[aid]);
        if (!(atom.fragment in fmap)) {
            fmap[atom.fragment] = action.addOp(new ui.Action.OpFragmentAdd().perform(ui.editor)).frid;
        }
        atom.fragment = fmap[atom.fragment];
        amap[aid] = action.addOp(new ui.Action.OpAtomAdd(atom, atom.pp.add(offset)).perform(ui.editor)).data.aid;
    }

    var rgnew = [];
    for (var rgid in ui.clipboard.rgroups) {
        if (!ui.ctab.rgroups.has(rgid)) {
            rgnew.push(rgid);
        }
    }
    
    // assign fragments to r-groups
    for (var frid in ui.clipboard.rgmap) {
        action.addOp(new ui.Action.OpRGroupFragment(ui.clipboard.rgmap[frid], fmap[frid]).perform(ui.editor));
    }
    
    for (var i = 0; i < rgnew.length; ++i) {
        action.mergeWith(ui.Action.fromRGroupAttrs(rgnew[i], ui.clipboard.rgroups[rgnew[i]]));
    }

    //bonds
    for (var bid = 0; bid < objects.bonds.length; bid++) {
        var bond = Object.clone(objects.bonds[bid]);
        action.addOp(new ui.Action.OpBondAdd(amap[bond.begin], amap[bond.end], bond).perform(ui.editor));
    }
    //sgroups
    for (var sgid = 0; sgid < objects.sgroups.length; sgid++) {
        var sgroup_info = objects.sgroups[sgid];
        var atoms = sgroup_info.atoms;
        var sgatoms = [];
        for (var sgaid = 0; sgaid < atoms.length; sgaid++) {
            sgatoms.push(amap[atoms[sgaid]]);
        }
        var newsgid = ui.render.ctab.molecule.sgroups.newId();
        var sgaction = ui.Action.fromSgroupAddition(sgroup_info.type, sgatoms, sgroup_info.attrs, newsgid);
        for (var iop = sgaction.operations.length - 1; iop >= 0; iop--) {
            action.addOp(sgaction.operations[iop]);
        }
    }
    //reaction arrows
    if (ui.editor.render.ctab.rxnArrows.count() < 1) {
        for (var raid = 0; raid < objects.rxnArrows.length; raid++) {
            action.addOp(new ui.Action.OpRxnArrowAdd(objects.rxnArrows[raid].pp.add(offset)).perform(ui.editor));
        }
    }
    //reaction pluses
    for (var rpid = 0; rpid < objects.rxnPluses.length; rpid++) {
        action.addOp(new ui.Action.OpRxnPlusAdd(objects.rxnPluses[rpid].pp.add(offset)).perform(ui.editor));
    }
    //thats all
    action.operations.reverse();
    return action;
};

ui.addUndoAction = function (action, check_dummy)
{
    if (action == null)
        return;

    if (check_dummy != true || !action.isDummy())
    {
        ui.undoStack.push(action);
        ui.redoStack.clear();
        if (ui.undoStack.length > ui.HISTORY_LENGTH)
            ui.undoStack.splice(0, 1);
        ui.updateActionButtons();
    }
};

ui.removeDummyAction = function ()
{
    if (ui.undoStack.length != 0 && ui.undoStack.last().isDummy())
    {
        ui.undoStack.pop();
        ui.updateActionButtons();
    }
};

ui.updateActionButtons = function ()
{
    if (ui.undoStack.length == 0)
        p$('undo').addClassName('buttonDisabled');
    else
        p$('undo').removeClassName('buttonDisabled');

    if (ui.redoStack.length == 0)
        p$('redo').addClassName('buttonDisabled');
    else
        p$('redo').removeClassName('buttonDisabled');
};

ui.undo = function ()
{
    if (this.render.current_tool)
        this.render.current_tool.OnCancel();

    ui.redoStack.push(ui.undoStack.pop().perform());
    ui.updateActionButtons();
    ui.updateSelection();
};

ui.redo = function ()
{
    if (this.render.current_tool)
        this.render.current_tool.OnCancel();

    ui.undoStack.push(ui.redoStack.pop().perform());
    ui.updateActionButtons();
    ui.updateSelection();
};


ui.Action.OpBase = function() {};
ui.Action.OpBase.prototype.type = 'OpBase';
ui.Action.OpBase.prototype._execute = function() {
    throw new Error('Operation._execute() is not implemented');
};
ui.Action.OpBase.prototype._invert = function() {
    throw new Error('Operation._invert() is not implemented');
};
ui.Action.OpBase.prototype.perform = function(editor) {
    this._execute(editor);
    if (!('__inverted' in this)) {
        this.__inverted = this._invert();
        this.__inverted.__inverted = this;
    }
    return this.__inverted;
};
ui.Action.OpBase.prototype.isDummy = function(editor) {
    return '_isDummy' in this ? this['_isDummy'](editor) : false;
};

ui.Action.OpAtomAdd = function(atom, pos) {
    this.data = { aid : null, atom : atom, pos : pos };
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        var pp = {};
        if (this.data.atom)
            for (var p in this.data.atom)
                pp[p] = this.data.atom[p];
        pp.label = pp.label || 'C';
        if (!Object.isNumber(this.data.aid)) {
            this.data.aid = DS.atoms.add(new chem.Struct.Atom(pp));
        } else {
            DS.atoms.set(this.data.aid, new chem.Struct.Atom(pp));
        }
        RS.notifyAtomAdded(this.data.aid);
        DS._atomSetPos(this.data.aid, new util.Vec2(this.data.pos));
    };
    this._invert = function() {
        var ret = new ui.Action.OpAtomDelete();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpAtomAdd.prototype = new ui.Action.OpBase();

ui.Action.OpAtomDelete = function(aid) {
    this.data = { aid : aid, atom : null, pos : null };
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        if (!this.data.atom) {
            this.data.atom = DS.atoms.get(this.data.aid);
            this.data.pos = R.atomGetPos(this.data.aid);
        }
        RS.notifyAtomRemoved(this.data.aid);
        DS.atoms.remove(this.data.aid);
    };
    this._invert = function() {
        var ret = new ui.Action.OpAtomAdd();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpAtomDelete.prototype = new ui.Action.OpBase();

ui.Action.OpAtomAttr = function(aid, attribute, value) {
    this.data = { aid : aid, attribute : attribute, value : value };
    this.data2 = null;
    this._execute = function(editor) {
        var atom = editor.render.ctab.molecule.atoms.get(this.data.aid);
        if (!this.data2) {
            this.data2 = { aid : this.data.aid, attribute : this.data.attribute, value : atom[this.data.attribute] };
        }

        if (this.data.attribute == 'label' && this.data.value != null) // HACK TODO review
            atom['atomList'] = null;

        atom[this.data.attribute] = this.data.value;

        editor.render.invalidateAtom(this.data.aid);
    };
    this._isDummy = function(editor) {
        return editor.render.ctab.molecule.atoms.get(this.data.aid)[this.data.attribute] == this.data.value;
    };
    this._invert = function() {
        var ret = new ui.Action.OpAtomAttr();
        ret.data = this.data2;
        ret.data2 = this.data;return ret;
    };
};
ui.Action.OpAtomAttr.prototype = new ui.Action.OpBase();

ui.Action.OpAtomMove = function(aid, d) {
    this.data = {aid : aid, d : d};
    this._execute = function(editor) {
        ui.ctab.atoms.get(this.data.aid).pp.add_(this.data.d);
        this.data.d = this.data.d.negated();
        editor.render.invalidateAtom(this.data.aid, 1);
    };
    this._invert = function() {
        var ret = new ui.Action.OpAtomMove();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpAtomMove.prototype = new ui.Action.OpBase();

ui.Action.OpSGroupAtomAdd = function(sgid, aid) {
    this.type = 'OpSGroupAtomAdd';
    this.data = {'aid' : aid, 'sgid' : sgid};
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        var aid = this.data.aid;
        var sgid = this.data.sgid;
	var atom = DS.atoms.get(aid);
	var sg = DS.sgroups.get(sgid);
	chem.SGroup.addAtom(sg, aid);
        if (!atom)
            throw new Error("OpSGroupAtomAdd: Atom " + aid + " not found");
	util.Set.add(atom.sgs, sgid);
        R.invalidateAtom(aid);
    };
    this._invert = function() {
        var ret = new ui.Action.OpSGroupAtomRemove();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpSGroupAtomAdd.prototype = new ui.Action.OpBase();

ui.Action.OpSGroupAtomRemove = function(sgid, aid) {
    this.type = 'OpSGroupAtomRemove';
    this.data = {'aid' : aid, 'sgid' : sgid};
    this._execute = function(editor) {
        var aid = this.data.aid;
        var sgid = this.data.sgid;
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
	var atom = DS.atoms.get(aid);
	var sg = DS.sgroups.get(sgid);
	chem.SGroup.removeAtom(sg, aid);
	util.Set.remove(atom.sgs, sgid);
        R.invalidateAtom(aid);
    };
    this._invert = function() {
        var ret = new ui.Action.OpSGroupAtomAdd();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpSGroupAtomRemove.prototype = new ui.Action.OpBase();

ui.Action.OpSGroupAttr = function(sgid, attr, value) {
    this.type = 'OpSGroupAttr';
    this.data = {sgid : sgid, attr : attr, value : value};
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        var sgid = this.data.sgid;
	var sg = DS.sgroups.get(sgid);
        if (sg.type == 'DAT' && RS.sgroupData.has(sgid)) { // clean the stuff here, else it might be left behind if the sgroups is set to "attached"
            RS.clearVisel(RS.sgroupData.get(sgid).visel);
            RS.sgroupData.unset(sgid);
        }

        this.data.value = sg.setAttr(this.data.attr, this.data.value);
    };
    this._invert = function() {
        var ret = new ui.Action.OpSGroupAttr();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpSGroupAttr.prototype = new ui.Action.OpBase();

ui.Action.OpSGroupCreate = function(sgid, type) {
    this.type = 'OpSGroupCreate';
    this.data = {'sgid' : sgid, 'type' : type};
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        var sg = new chem.SGroup(this.data.type);
        var sgid = this.data.sgid;
        sg.id = sgid;
        DS.sgroups.set(sgid, sg);
        RS.sgroups.set(sgid, new rnd.ReSGroup(DS.sgroups.get(sgid)));
        this.data.sgid = sgid;
    };
    this._invert = function() {
        var ret = new ui.Action.OpSGroupDelete();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpSGroupCreate.prototype = new ui.Action.OpBase();

ui.Action.OpSGroupDelete = function(sgid) {
    this.type = 'OpSGroupDelete';
    this.data = {'sgid' : sgid};
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        var sgid = this.data.sgid;
        var sg = RS.sgroups.get(sgid);
        this.data.type = sg.item.type;
        if (sg.item.type == 'DAT' && RS.sgroupData.has(sgid)) {
            RS.clearVisel(RS.sgroupData.get(sgid).visel);
            RS.sgroupData.unset(sgid);
        }

        RS.clearVisel(sg.visel);
        if (sg.item.atoms.length != 0)
            throw new Error("S-Group not empty!");
        RS.sgroups.unset(sgid);
        DS.sgroups.remove(sgid);
    };
    this._invert = function() {
        var ret = new ui.Action.OpSGroupCreate();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpSGroupDelete.prototype = new ui.Action.OpBase();

ui.Action.OpBondAdd = function(begin, end, bond) {
    this.data = { bid : null, bond : bond, begin : begin, end : end };
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        if (this.data.begin == this.data.end)
            throw new Error("Distinct atoms expected");
        if (rnd.DEBUG && this.molecule.checkBondExists(this.data.begin, this.data.end))
            throw new Error("Bond already exists");

        R.invalidateAtom(this.data.begin, 1);
        R.invalidateAtom(this.data.end, 1);

        var pp = {};
        if (this.data.bond)
            for (var p in this.data.bond)
                pp[p] = this.data.bond[p];
        pp.type = pp.type || chem.Struct.BOND.TYPE.SINGLE;
        pp.begin = this.data.begin;
        pp.end = this.data.end;

        if (!Object.isNumber(this.data.bid)) {
            this.data.bid = DS.bonds.add(new chem.Struct.Bond(pp));
        } else {
            DS.bonds.set(this.data.bid, new chem.Struct.Bond(pp));
        }
        DS.bondInitHalfBonds(this.data.bid);
        DS.atomAddNeighbor(DS.bonds.get(this.data.bid).hb1);
        DS.atomAddNeighbor(DS.bonds.get(this.data.bid).hb2);

        RS.notifyBondAdded(this.data.bid);
    };
    this._invert = function() {
        var ret = new ui.Action.OpBondDelete();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpBondAdd.prototype = new ui.Action.OpBase();

ui.Action.OpBondDelete = function(bid) {
    this.data = { bid : bid, bond : null, begin : null, end : null };
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        if (!this.data.bond) {
            this.data.bond = DS.bonds.get(this.data.bid);
            this.data.begin = this.data.bond.begin;
            this.data.end = this.data.bond.end;
        }

        R.invalidateBond(this.data.bid);

        RS.notifyBondRemoved(this.data.bid);

        var bond = DS.bonds.get(this.data.bid);
        [bond.hb1, bond.hb2].each(function(hbid) {
            var hb = DS.halfBonds.get(hbid);
            var atom = DS.atoms.get(hb.begin);
            var pos = atom.neighbors.indexOf(hbid);
            var prev = (pos + atom.neighbors.length - 1) % atom.neighbors.length;
            var next = (pos + 1) % atom.neighbors.length;
            DS.setHbNext(atom.neighbors[prev], atom.neighbors[next]);
            atom.neighbors.splice(pos, 1);
        }, this);
        DS.halfBonds.unset(bond.hb1);
        DS.halfBonds.unset(bond.hb2);

        DS.bonds.remove(this.data.bid);
    };
    this._invert = function() {
        var ret = new ui.Action.OpBondAdd();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpBondDelete.prototype = new ui.Action.OpBase();

ui.Action.OpBondAttr = function(bid, attribute, value) {
    this.data = { bid : bid, attribute : attribute, value : value };
    this.data2 = null;
    this._execute = function(editor) {
        var bond = editor.render.ctab.molecule.bonds.get(this.data.bid);
        if (!this.data2) {
            this.data2 = { bid : this.data.bid, attribute : this.data.attribute, value : bond[this.data.attribute] };
        }

        bond[this.data.attribute] = this.data.value;

        editor.render.invalidateBond(this.data.bid, this.data.attribute == 'type' ? 1 : 0);
    };
    this._isDummy = function(editor) {
        return editor.render.ctab.molecule.bonds.get(this.data.bid)[this.data.attribute] == this.data.value;
    };
    this._invert = function() {
        var ret = new ui.Action.OpBondAttr();
        ret.data = this.data2;
        ret.data2 = this.data;
        return ret;
    };
};
ui.Action.OpBondAttr.prototype = new ui.Action.OpBase();

ui.Action.OpFragmentAdd = function(frid) {
    this.frid = Object.isUndefined(frid) ? null : frid;
    this._execute = function(editor) {
        var RS = editor.render.ctab, DS = RS.molecule;
        var frag = new chem.Struct.Fragment();
        if (this.frid == null) {
            this.frid = DS.frags.add(frag);
        } else {
            DS.frags.set(this.frid, frag);
        }
        RS.frags.set(this.frid, new rnd.ReFrag(frag)); // TODO add ReStruct.notifyFragmentAdded
    };
    this._invert = function() {
        return new ui.Action.OpFragmentDelete(this.frid);
    };
};
ui.Action.OpFragmentAdd.prototype = new ui.Action.OpBase();

ui.Action.OpFragmentDelete = function(frid) {
    this.frid = frid;
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        R.invalidateItem('frags', this.frid, 1);
        RS.frags.unset(this.frid);
        DS.frags.remove(this.frid); // TODO add ReStruct.notifyFragmentRemoved
    };
    this._invert = function() {
        return new ui.Action.OpFragmentAdd(this.frid);
    };
};
ui.Action.OpFragmentDelete.prototype = new ui.Action.OpBase();

ui.Action.OpRGroupAttr = function(rgid, attribute, value) {
    this.data = { rgid : rgid, attribute : attribute, value : value };
    this.data2 = null;
    this._execute = function(editor) {
        var rgp = editor.render.ctab.molecule.rgroups.get(this.data.rgid);
        if (!this.data2) {
            this.data2 = { rgid : this.data.rgid, attribute : this.data.attribute, value : rgp[this.data.attribute] };
        }

        rgp[this.data.attribute] = this.data.value;

        editor.render.invalidateItem('rgroups', this.data.rgid);
    };
    this._isDummy = function(editor) {
        return editor.render.ctab.molecule.rgroups.get(this.data.rgid)[this.data.attribute] == this.data.value;
    };
    this._invert = function() {
        var ret = new ui.Action.OpRGroupAttr();
        ret.data = this.data2;
        ret.data2 = this.data;
        return ret;
    };
};
ui.Action.OpRGroupAttr.prototype = new ui.Action.OpBase();

ui.Action.OpRGroupFragment = function(rgid, frid) {
    this.rgid_new = rgid;
    this.rgid_old = null;
    this.frid = frid;
    this._execute = function(editor) {
        var RS = editor.render.ctab, DS = RS.molecule;
        this.rgid_old = this.rgid_old || chem.Struct.RGroup.findRGroupByFragment(DS.rgroups, this.frid);

        var rgOld = (this.rgid_old ? DS.rgroups.get(this.rgid_old) : null);
        if (rgOld) {
            rgOld.frags.remove(rgOld.frags.keyOf(this.frid));
            RS.clearVisel(RS.rgroups.get(this.rgid_old).visel);
            if (rgOld.frags.count() == 0) {
                RS.rgroups.unset(this.rgid_old);
                DS.rgroups.unset(this.rgid_old);
                RS.markItemRemoved();
            } else {
                RS.markItem('rgroups', this.rgid_old, 1);
            }
        }
        if (this.rgid_new) {
            var rgNew = DS.rgroups.get(this.rgid_new);
            if (!rgNew) {
                rgNew = new chem.Struct.RGroup();
                DS.rgroups.set(this.rgid_new, rgNew);
                RS.rgroups.set(this.rgid_new, new rnd.ReRGroup(rgNew));
            } else {
                RS.markItem('rgroups', this.rgid_new, 1);
            }
            rgNew.frags.add(this.frid);
        }
    };
    this._invert = function() {
        return new ui.Action.OpRGroupFragment(this.rgid_old, this.frid);
    };
};
ui.Action.OpRGroupFragment.prototype = new ui.Action.OpBase();

ui.Action.OpRxnArrowAdd = function(pos) {
    this.data = { arid : null, pos : pos };
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        if (!Object.isNumber(this.data.arid)) {
            this.data.arid = DS.rxnArrows.add(new chem.Struct.RxnArrow());
        } else {
            DS.rxnArrows.set(this.data.arid, new chem.Struct.RxnArrow());
        }
        RS.notifyRxnArrowAdded(this.data.arid);
        DS._rxnArrowSetPos(this.data.arid, new util.Vec2(this.data.pos));

        R.invalidateItem('rxnArrows', this.data.arid, 1);
    };
    this._invert = function() {
        var ret = new ui.Action.OpRxnArrowDelete();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpRxnArrowAdd.prototype = new ui.Action.OpBase();

ui.Action.OpRxnArrowDelete = function(arid) {
    this.data = { arid : arid, pos : null };
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        if (!this.data.pos) {
            this.data.pos = R.rxnArrowGetPos(this.data.arid);
        }
        RS.notifyRxnArrowRemoved(this.data.arid);
        DS.rxnArrows.remove(this.data.arid);
    };
    this._invert = function() {
        var ret = new ui.Action.OpRxnArrowAdd();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpRxnArrowDelete.prototype = new ui.Action.OpBase();

ui.Action.OpRxnArrowMove = function(id, d) {
    this.data = {id : id, d : d};
    this._execute = function(editor) {
        ui.ctab.rxnArrows.get(this.data.id).pp.add_(this.data.d);
        this.data.d = this.data.d.negated();
        editor.render.invalidateItem('rxnArrows', this.data.id, 1);
    };
    this._invert = function() {
        var ret = new ui.Action.OpRxnArrowMove();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpRxnArrowMove.prototype = new ui.Action.OpBase();

ui.Action.OpRxnPlusAdd = function(pos) {
    this.data = { plid : null, pos : pos };
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        if (!Object.isNumber(this.data.plid)) {
            this.data.plid = DS.rxnPluses.add(new chem.Struct.RxnPlus());
        } else {
            DS.rxnPluses.set(this.data.plid, new chem.Struct.RxnPlus());
        }
        RS.notifyRxnPlusAdded(this.data.plid);
        DS._rxnPlusSetPos(this.data.plid, new util.Vec2(this.data.pos));

        R.invalidateItem('rxnPluses', this.data.plid, 1);
    };
    this._invert = function() {
        var ret = new ui.Action.OpRxnPlusDelete();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpRxnPlusAdd.prototype = new ui.Action.OpBase();

ui.Action.OpRxnPlusDelete = function(plid) {
    this.data = { plid : plid, pos : null };
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        if (!this.data.pos) {
            this.data.pos = R.rxnPlusGetPos(this.data.plid);
        }
        RS.notifyRxnPlusRemoved(this.data.plid);
        DS.rxnPluses.remove(this.data.plid);
    };
    this._invert = function() {
        var ret = new ui.Action.OpRxnPlusAdd();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpRxnPlusDelete.prototype = new ui.Action.OpBase();

ui.Action.OpRxnPlusMove = function(id, d) {
    this.data = {id : id, d : d};
    this._execute = function(editor) {
        ui.ctab.rxnPluses.get(this.data.id).pp.add_(this.data.d);
        this.data.d = this.data.d.negated();
        editor.render.invalidateItem('rxnPluses', this.data.id, 1);
    };
    this._invert = function() {
        var ret = new ui.Action.OpRxnPlusMove();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpRxnPlusMove.prototype = new ui.Action.OpBase();

ui.Action.OpSGroupDataMove = function(id, d) {
    this.data = {id : id, d : d};
    this._execute = function(editor) {
        ui.ctab.sgroups.get(this.data.id).pp.add_(this.data.d);
        this.data.d = this.data.d.negated();
        editor.render.invalidateItem('sgroupData', this.data.id, 1); // [MK] this currently does nothing since the DataSGroupData Visel only contains the highlighting/selection and SGroups are redrawn every time anyway
    };
    this._invert = function() {
        var ret = new ui.Action.OpSGroupDataMove();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpSGroupDataMove.prototype = new ui.Action.OpBase();

ui.Action.OpCanvasLoad = function(ctab) {
    this.data = {ctab : ctab, norescale : false};
    this._execute = function(editor) {
        var R = editor.render;

        R.ctab.clearVisels();
        var oldCtab = ui.ctab;
        ui.ctab = this.data.ctab;
        R.setMolecule(ui.ctab, this.data.norescale);
        this.data.ctab = oldCtab;
        this.data.norescale = true;
    };

    this._invert = function() {
        var ret = new ui.Action.OpCanvasLoad();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpCanvasLoad.prototype = new ui.Action.OpBase();

ui.Action.OpChiralFlagAdd = function(pos) {
    this.data = {pos : pos};
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        if (RS.chiralFlags.count() > 0)
            throw new Error("Cannot add more than one Chiral flag");
        RS.chiralFlags.set(0, new rnd.ReChiralFlag(pos));
        DS.isChiral = true;
        R.invalidateItem('chiralFlags', 0, 1);
    };
    this._invert = function() {
        var ret = new ui.Action.OpChiralFlagDelete();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpChiralFlagAdd.prototype = new ui.Action.OpBase();

ui.Action.OpChiralFlagDelete = function() {
    this.data = {pos : null};
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab, DS = RS.molecule;
        if (RS.chiralFlags.count() < 1)
            throw new Error("Cannot remove chiral flag");
        RS.clearVisel(RS.chiralFlags.get(0).visel);
        this.data.pos = RS.chiralFlags.get(0).pp;
        RS.chiralFlags.unset(0);
        DS.isChiral = false;
    };
    this._invert = function() {
        var ret = new ui.Action.OpChiralFlagAdd(this.data.pos);
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpChiralFlagDelete.prototype = new ui.Action.OpBase();

ui.Action.OpChiralFlagMove = function(d) {
    this.data = {d : d};
    this._execute = function(editor) {
        var R = editor.render, RS = R.ctab;
        RS.chiralFlags.get(0).pp.add_(this.data.d);
        this.data.d = this.data.d.negated();
        R.invalidateItem('chiralFlags', 0, 1);
    };
    this._invert = function() {
        var ret = new ui.Action.OpChiralFlagMove();
        ret.data = this.data;
        return ret;
    };
};
ui.Action.OpChiralFlagMove.prototype = new ui.Action.OpBase();
if (!window.Prototype)
	throw new Error("Prototype.js should be loaded first");
if (!window.rnd)
	throw new Error("rnd should be defined prior to loading this file");
if (!window.ui)
	throw new Error("ui should be defined prior to loading this file");

rnd.ReaGenericsTable = function (clientArea, opts) {
	opts = opts || {};
	clientArea = p$(clientArea);
    clientArea.style.width = '610px';
    clientArea.style.height = '390px';
	clientArea.innerHTML = "";

	this.onClick = function(text) {
        this.setSelection(text);
	};

    this.elemHalfSz = new util.Vec2(22, 14);
    this.elemSz = this.elemHalfSz.scaled(2);

	this.spacing = new util.Vec2(3, 3);
	this.cornerRadius = 2;
	this.orig = this.elemSz.scaled(0);

	this.viewSz = new util.Vec2(clientArea['clientWidth'] || 100, clientArea['clientHeight'] || 100);

	this.paper = new Raphael(clientArea, this.viewSz.x, this.viewSz.y);
	this.bb = new util.Box2Abs(new util.Vec2(), this.viewSz);

    this.bgColor = clientArea.getStyle('background-color');
	this.fillColor = opts.fillColor || '#def';
	this.fillColorSelected = opts.fillColorSelected || '#fcb';
	this.frameColor = opts.frameColor || '#9ad';
	this.frameThickness = opts.frameThickness || '1pt';
	this.fontSize = opts.fontSize || 18;
	this.fontType = opts.fontType || "Arial";
	this.atomProps = null;

	this.frameAttrs = {
        'fill':this.fillColor,
        'stroke':this.frameColor,
        'stroke-width':this.frameThickness
    };
	this.fontAttrs = {
        'font-family': this.fontType,
        'font-size': this.fontSize
    };
    this.groupRectAttrs = {
        'stroke' : 'lightgray',
        'stroke-width' : '1px'
    };
    this.labelTextAttrs = {
        'font-family': "Arial",
        'font-size': 13,
        'fill' : 'gray'
    };
    this.labelRectAttrs = {
        fill : this.bgColor,
        stroke : this.bgColor
    };
    this.items = [];

    var drawGroup = function(x, y, w, h, text, align) {
        align = align || 'start';
        this.paper.rect(x, y, w, h, this.cornerRadius).attr(this.groupRectAttrs);
        var t = this.paper.text(
            align == 'left' ? x + 10 : align == 'right' ? x + w - 10 : x + w / 2,
            y,
            text
        ).attr(
            this.labelTextAttrs
        ).attr(
            'text-anchor',
            align == 'left' ? 'start' : align == 'right' ? 'end' : 'middle'
        );
        this.paper.rect().attr(t.getBBox()).attr(this.labelRectAttrs);
        t.toFront();
    };
    var drawLabel = function(x, y, text, align) {
        this.paper.text(x, y, text).attr(this.labelTextAttrs).attr(
            'text-anchor',
            align == 'left' ? 'start' : align == 'right' ? 'end' : 'middle'
        );
    };
    var drawButton = function(center, text) {
        var box = this.paper.rect(
            center.x - this.elemHalfSz.x, center.y - this.elemHalfSz.y, this.elemSz.x, this.elemSz.y, this.cornerRadius
        ).attr(this.frameAttrs);
        var label = this.paper.text(center.x, center.y, text).attr(this.fontAttrs);
        var self = this;
        box.node.onclick = function() {
            self.onClick(text);
        };
        label.node.onclick = function() {
            self.onClick(text);
        };
        this.items.push({ text : text, box : box, label : label });
    };

    var zx = (this.spacing.x + this.elemSz.x);

    drawGroup.apply(this, [5, 5, 600, 75, 'Atom Generics', 'left']);
    drawButton.apply(this, [new util.Vec2(57, 30), 'A']);
    drawButton.apply(this, [new util.Vec2(57 + zx, 30), 'AH']);
    drawLabel.apply(this, [81, 60, 'any atom\n\t']);
    drawButton.apply(this, [new util.Vec2(207, 30), 'Q']);
    drawButton.apply(this, [new util.Vec2(207 + zx, 30), 'QH']);
    drawLabel.apply(this, [231, 60, 'any atom except\ncarbon or hydrogen']);
    drawButton.apply(this, [new util.Vec2(357, 30), 'M']);
    drawButton.apply(this, [new util.Vec2(357 + zx, 30), 'MH']);
    drawLabel.apply(this, [381, 60, 'any metal\n\t']);
    drawButton.apply(this, [new util.Vec2(507, 30), 'X']);
    drawButton.apply(this, [new util.Vec2(507 + zx, 30), 'XH']);
    drawLabel.apply(this, [531, 60, 'any halogen\n\t']);

    drawGroup.apply(this, [5, 90, 600, 300, 'Group Generics', 'left']);
    drawLabel.apply(this, [210, 115, 'any', 'right']);
    drawButton.apply(this, [new util.Vec2(286 - zx, 115), 'G']);
    drawButton.apply(this, [new util.Vec2(286, 115), 'GH']);
    drawButton.apply(this, [new util.Vec2(286 + zx, 115), 'G*']);
    drawButton.apply(this, [new util.Vec2(286 + 2 * zx, 115), 'GH*']);

    drawGroup.apply(this, [10, 140, 235, 245, 'ACYCLIC']);
    drawLabel.apply(this, [74, 165, 'acyclic', 'right']);
    drawButton.apply(this, [new util.Vec2(104, 165), 'ACY']);
    drawButton.apply(this, [new util.Vec2(104 + zx, 165), 'ACH']);

    drawGroup.apply(this, [15, 190, 110, 190, 'CARB']);
    drawButton.apply(this, [new util.Vec2(46, 215), 'ABC']);
    drawButton.apply(this, [new util.Vec2(46 + zx, 215), 'ABH']);
    drawLabel.apply(this, [68, 235, 'carb']);
    drawButton.apply(this, [new util.Vec2(46, 260), 'AYL']);
    drawButton.apply(this, [new util.Vec2(46 + zx, 260), 'AYH']);
    drawLabel.apply(this, [68, 280, 'alkynyl']);
    drawButton.apply(this, [new util.Vec2(46, 305), 'ALK']);
    drawButton.apply(this, [new util.Vec2(46 + zx, 305), 'ALH']);
    drawLabel.apply(this, [68, 325, 'alkyl']);
    drawButton.apply(this, [new util.Vec2(46, 350), 'AEL']);
    drawButton.apply(this, [new util.Vec2(46 + zx, 350), 'AEH']);
    drawLabel.apply(this, [68, 370, 'alkenyl']);

    drawGroup.apply(this, [130, 190, 110, 190, 'HETERO']);
    drawButton.apply(this, [new util.Vec2(161, 215), 'AHC']);
    drawButton.apply(this, [new util.Vec2(161 + zx, 215), 'AHH']);
    drawLabel.apply(this, [183, 235, 'hetero']);
    drawButton.apply(this, [new util.Vec2(161, 260), 'AOX']);
    drawButton.apply(this, [new util.Vec2(161 + zx, 260), 'AOH']);
    drawLabel.apply(this, [183, 280, 'alkoxy']);

    drawGroup.apply(this, [250, 140, 350, 245, 'CYCLIC']);
    drawLabel.apply(this, [371, 165, 'cyclic', 'right']);
    drawButton.apply(this, [new util.Vec2(401, 165), 'CYC']);
    drawButton.apply(this, [new util.Vec2(401 + zx, 165), 'CYH']);

    drawGroup.apply(this, [255, 190, 110, 190, 'CARBO']);
    drawButton.apply(this, [new util.Vec2(286, 215), 'CBC']);
    drawButton.apply(this, [new util.Vec2(286 + zx, 215), 'CBH']);
    drawLabel.apply(this, [308, 235, 'carbo']);
    drawButton.apply(this, [new util.Vec2(286, 260), 'ARY']);
    drawButton.apply(this, [new util.Vec2(286 + zx, 260), 'ARH']);
    drawLabel.apply(this, [308, 280, 'aryl']);
    drawButton.apply(this, [new util.Vec2(286, 305), 'CAL']);
    drawButton.apply(this, [new util.Vec2(286 + zx, 305), 'CAH']);
    drawLabel.apply(this, [308, 325, 'cycloalkyl']);
    drawButton.apply(this, [new util.Vec2(286, 350), 'CEL']);
    drawButton.apply(this, [new util.Vec2(286 + zx, 350), 'CEH']);
    drawLabel.apply(this, [308, 370, 'cycloalkenyl']);

    drawGroup.apply(this, [370, 190, 110, 190, 'HETERO']);
    drawButton.apply(this, [new util.Vec2(401, 215), 'CHC']);
    drawButton.apply(this, [new util.Vec2(401 + zx, 215), 'CHH']);
    drawLabel.apply(this, [423, 235, 'hetero']);
    drawButton.apply(this, [new util.Vec2(401, 260), 'HAR']);
    drawButton.apply(this, [new util.Vec2(401 + zx, 260), 'HAH']);
    drawLabel.apply(this, [423, 280, 'hetero aryl']);

    drawGroup.apply(this, [485, 190, 110, 190, 'CYCLIC']);
    drawButton.apply(this, [new util.Vec2(516, 215), 'CXX']);
    drawButton.apply(this, [new util.Vec2(516 + zx, 215), 'CXH']);
    drawLabel.apply(this, [538, 235, 'no carbon']);
};

rnd.ReaGenericsTable.prototype.getAtomProps = function () {
	return this.atomProps;
};

rnd.ReaGenericsTable.prototype.setSelection = function(selection) {
    this.atomProps = { label : selection };
    for (var i = 0; i < this.items.length; i++) {
        this.items[i].box.attr('fill', this.items[i].text == selection ? this.fillColorSelected : this.fillColor);
    }
    p$('reagenerics_table_ok').disabled = (!selection || selection == '');
};


ui.showReaGenericsTable = function(params) {
    if (!p$('reagenerics_table').visible()) {
        params = params || {};
        ui.showDialog('reagenerics_table');
        if (typeof(ui.reagenerics_table_obj) == 'undefined') {
            ui.reagenerics_table_obj = new rnd.ReaGenericsTable('reagenerics_table_area', {
                'fillColor':'#DADADA',
                'fillColorSelected':'#FFFFFF',
                'frameColor':'#E8E8E8',
                'fontSize':18,
                'buttonHalfSize':18
            }, true);
        }
        if (params.selection)
            ui.reagenerics_table_obj.setSelection(params.selection);
        var _onOk = new Event.Handler('reagenerics_table_ok', 'click', undefined, function() {
            if (ui.reagenerics_table_obj.atomProps == null)
                return;
            ui.hideDialog('reagenerics_table');
            if ('onOk' in params) params['onOk'](ui.reagenerics_table_obj.selection);
            _onOk.stop();
        }).start();
        var _onCancel = new Event.Handler('reagenerics_table_cancel', 'click', undefined, function() {
            ui.hideDialog('reagenerics_table');
            if ('onCancel' in params) params['onCancel']();
            _onCancel.stop();
        }).start();
        p$($('reagenerics_table_ok').disabled ? 'reagenerics_table_cancel' : 'reagenerics_table_ok').focus();
    }
};


ui.onClick_ReaGenericsTableButton = function ()
{
    if (this.hasClassName('buttonDisabled'))
        return;
    ui.showReaGenericsTable({
        onOk : function() {
            ui.selectMode('atom_reagenerics');
        }
    });
};

/****************************************************************************
 * Copyright (C) 2009-2010 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU Affero General Public License version 3 as published by the Free
 * Software Foundation and appearing in the file LICENSE.GPL included in
 * the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/

ketcher = function () {
    this.render = null;
};

ketcher.version = "1.0b5";

ketcher.init = function (settings)
{
  ketcher.templates = {};
	ketcher.button_areas = {};
	
	var elemLabelOpts = {'fontSize':25};
	ketcher.button_areas.atom_h = new rnd.ElementTable('atom_h', elemLabelOpts).renderSingle('H');
	ketcher.button_areas.atom_c = new rnd.ElementTable('atom_c', elemLabelOpts).renderSingle('C');
	ketcher.button_areas.atom_n = new rnd.ElementTable('atom_n', elemLabelOpts).renderSingle('N');
	ketcher.button_areas.atom_o = new rnd.ElementTable('atom_o', elemLabelOpts).renderSingle('O');
	ketcher.button_areas.atom_s = new rnd.ElementTable('atom_s', elemLabelOpts).renderSingle('S');
	ketcher.button_areas.atom_p = new rnd.ElementTable('atom_p', elemLabelOpts).renderSingle('P');
	ketcher.button_areas.atom_f = new rnd.ElementTable('atom_f', elemLabelOpts).renderSingle('F');
	ketcher.button_areas.atom_cl = new rnd.ElementTable('atom_cl', elemLabelOpts).renderSingle('Cl');
	ketcher.button_areas.atom_br = new rnd.ElementTable('atom_br', elemLabelOpts).renderSingle('Br');
	ketcher.button_areas.atom_i = new rnd.ElementTable('atom_i', elemLabelOpts).renderSingle('I');
	ketcher.button_areas.atom_table = new rnd.ElementTable('atom_table', elemLabelOpts).renderSingle('...');
	ketcher.button_areas.atom_any = new rnd.ElementTable('atom_reagenerics', {'fontSize':9}).renderSingle('Generic\nGroups');

	var charge_head = ['', '  fun stuff 0123456789AB', '',
		'  1  0  0  0  0  0            999 V2000',
		'    0.4714    1.8562    0.0000 A   0  3  0  0  0  0  0  0  0  0  0  0'];
	var charge_tail = ['M  END'];

    var tmpl = ketcher.templates;
	tmpl.charge_plus = charge_head.concat(['M  CHG  1   1   1'], charge_tail);
	tmpl.charge_minus = charge_head.concat(['M  CHG  1   1  -1'], charge_tail);

	var renderOpts = {
		'autoScale':true,
		'autoScaleMargin':2,
		'hideImplicitHydrogen':true,
		'hideTerminalLabels':true,
		'ignoreMouseEvents':true};

	var renderOptsBond = {
		'autoScale':true,
		'autoScaleMargin':4,
		'hideImplicitHydrogen':true,
		'hideTerminalLabels':true,
		'ignoreMouseEvents':true};

	ketcher.button_areas.charge_plus = ketcher.showMolfileOpts('charge_plus', tmpl.charge_plus, 75, renderOpts);
	ketcher.button_areas.charge_minus = ketcher.showMolfileOpts('charge_minus', tmpl.charge_minus, 75, renderOpts);

	var bond_head = ['', '  Ketcher 08191119302D 1   1.00000     0.00000     0', '',
		'  2  1  0     0  0            999 V2000',
		'   -2.5000   -0.3000    0.0000 C   0  0  0  0  0  0  0        0  0  0',
		'   -1.0000    0.3000    0.0000 C   0  0  0  0  0  0  0        0  0  0'];
	var bond_tail = ['M  END'];

    // will use this templates in dropdown list
	tmpl.bond_any = bond_head.concat(['  1  2  8  0     0  0'], bond_tail);
	tmpl.bond_single = bond_head.concat(['  1  2  1  0     0  0'], bond_tail);
	tmpl.bond_up = bond_head.concat(['  1  2  1  1     0  0'], bond_tail);
	tmpl.bond_down = bond_head.concat(['  1  2  1  6     0  0'], bond_tail);
	tmpl.bond_updown = bond_head.concat(['  1  2  1  4     0  0'], bond_tail);
	tmpl.bond_double  = bond_head.concat(['  1  2  2  0     0  0'], bond_tail);
	tmpl.bond_crossed  = bond_head.concat(['  1  2  2  3     0  0'], bond_tail);
	tmpl.bond_triple = bond_head.concat(['  1  2  3  0     0  0'], bond_tail);
	tmpl.bond_aromatic = bond_head.concat(['  1  2  4  0     0  0'], bond_tail);
	tmpl.bond_singledouble = bond_head.concat(['  1  2  5  0     0  0'], bond_tail);
	tmpl.bond_singlearomatic = bond_head.concat(['  1  2  6  0     0  0'], bond_tail);
	tmpl.bond_doublearomatic = bond_head.concat(['  1  2  7  0     0  0'], bond_tail);

	ketcher.button_areas.bond_single = ketcher.showMolfileOpts('bond', tmpl.bond_single, 20, renderOptsBond);

	var renderOptsPattern = {
		'autoScale':true,
		'autoScaleMargin':2,
		'hideImplicitHydrogen':true,
		'hideTerminalLabels':true,
		'ignoreMouseEvents':true};

	tmpl.chain = ['', '  Ketcher 10181123552D 1   1.00000     0.00000     0', '',
		'  4  3  0     0  0            999 V2000',
		'   -5.8000   -0.6500    0.0000 C   0  0  0  0  0  0        0  0  0',
		'   -4.9340   -1.1500    0.0000 C   0  0  0  0  0  0        0  0  0',
		'   -4.0679   -0.6500    0.0000 C   0  0  0  0  0  0        0  0  0',
		'   -3.2019   -1.1500    0.0000 C   0  0  0  0  0  0        0  0  0',
		'  1  2  1  0     0  0',
		'  2  3  1  0     0  0',
		'  3  4  1  0     0  0',
		'M  END'
	];

	tmpl.template_0 = ['', '  Ketcher 12101120452D 1   1.00000     0.00000     0', '',
        '  6  6  0     0  0            999 V2000',
        '    0.8660    2.0000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.7320    1.5000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.7320    0.5000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.8660    0.0000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.0000    0.5000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.0000    1.5000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '  1  2  1  0     0  0',
        '  2  3  2  0     0  0',
        '  3  4  1  0     0  0',
        '  4  5  2  0     0  0',
        '  5  6  1  0     0  0',
        '  6  1  2  0     0  0',
        'M  END'
    ];
    tmpl.template_1 = ['', '  Ketcher 12101117232D 1   1.00000     0.00000     0', '',
        '  5  5  0     0  0            999 V2000',
        '    0.0000    1.4257    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.8090    0.8379    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.5000   -0.1132    0.0000 C   0  0  0  0  0  0        0  0  0',
        '   -0.5000   -0.1132    0.0000 C   0  0  0  0  0  0        0  0  0',
        '   -0.8090    0.8379    0.0000 C   0  0  0  0  0  0        0  0  0',
        '  1  2  1  0     0  0',
        '  2  3  2  0     0  0',
        '  3  4  1  0     0  0',
        '  4  5  2  0     0  0',
        '  5  1  1  0     0  0',
        'M  END'
    ];
	tmpl.template_2 = ['', '  Ketcher 12101120472D 1   1.00000     0.00000     0', '',
        '  6  6  0     0  0            999 V2000',
        '    0.8660    2.0000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.7320    1.5000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.7320    0.5000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.8660    0.0000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.0000    0.5000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.0000    1.5000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '  1  2  1  0     0  0',
        '  2  3  1  0     0  0',
        '  3  4  1  0     0  0',
        '  4  5  1  0     0  0',
        '  5  6  1  0     0  0',
        '  6  1  1  0     0  0',
        'M  END'
    ];
	tmpl.template_3 = ['', '  Ketcher 12101120492D 1   1.00000     0.00000     0', '',
        '  5  5  0     0  0            999 V2000',
        '    0.8090    1.5389    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.6180    0.9511    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.3090    0.0000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.3090    0.0000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.0000    0.9511    0.0000 C   0  0  0  0  0  0        0  0  0',
        '  1  2  1  0     0  0',
        '  2  3  1  0     0  0',
        '  3  4  1  0     0  0',
        '  4  5  1  0     0  0',
        '  5  1  1  0     0  0',
        'M  END'
    ];
    tmpl.template_4 = ['', '  Ketcher 12101118272D 1   1.00000     0.00000     0', '',
        '  3  3  0     0  0            999 V2000',
        '   -3.2250   -0.2750    0.0000 C   0  0  0  0  0  0        0  0  0',
        '   -2.2250   -0.2750    0.0000 C   0  0  0  0  0  0        0  0  0',
        '   -2.7250    0.5910    0.0000 C   0  0  0  0  0  0        0  0  0',
        '  1  2  1  0     0  0',
        '  2  3  1  0     0  0',
        '  1  3  1  0     0  0',
        'M  END'
    ];
    tmpl.template_5 = ['', '  Ketcher 12101118312D 1   1.00000     0.00000     0', '',
        '  4  4  0     0  0            999 V2000',
        '   -3.8250    1.5500    0.0000 C   0  0  0  0  0  0        0  0  0',
        '   -3.8250    0.5500    0.0000 C   0  0  0  0  0  0        0  0  0',
        '   -2.8250    1.5500    0.0000 C   0  0  0  0  0  0        0  0  0',
        '   -2.8250    0.5500    0.0000 C   0  0  0  0  0  0        0  0  0',
        '  1  2  1  0     0  0',
        '  1  3  1  0     0  0',
        '  3  4  1  0     0  0',
        '  4  2  1  0     0  0',
        'M  END'
    ];
    tmpl.template_6 = ['', '  Ketcher 12101118372D 1   1.00000     0.00000     0', '',
        '  7  7  0     0  0            999 V2000',
        '    0.0000    1.6293    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.7835    2.2465    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.7559    2.0242    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    2.1897    1.1289    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.0000    0.6228    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.7566    0.2224    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.7835    0.0000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '  6  7  1  0     0  0',
        '  5  7  1  0     0  0',
        '  1  5  1  0     0  0',
        '  4  6  1  0     0  0',
        '  3  4  1  0     0  0',
        '  2  3  1  0     0  0',
        '  1  2  1  0     0  0',
        'M  END'
    ];
    tmpl.template_7 = ['', '  Ketcher 12101118392D 1   1.00000     0.00000     0', '',
        '  8  8  0     0  0            999 V2000',
        '    0.0000    0.7053    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.0000    1.7078    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.7053    2.4131    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    0.7056    0.0000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.7079    0.0000    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    2.4133    0.7053    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    2.4133    1.7078    0.0000 C   0  0  0  0  0  0        0  0  0',
        '    1.7079    2.4131    0.0000 C   0  0  0  0  0  0        0  0  0',
        '  8  3  1  0     0  0',
        '  7  8  1  0     0  0',
        '  6  7  1  0     0  0',
        '  5  6  1  0     0  0',
        '  4  5  1  0     0  0',
        '  1  4  1  0     0  0',
        '  2  3  1  0     0  0',
        '  1  2  1  0     0  0',
        'M  END'
    ];

	ketcher.button_areas.chain = ketcher.showMolfileOpts('chain', tmpl.chain, 20, renderOptsPattern);
    ketcher.button_areas.template_0 = ketcher.showMolfileOpts('template', tmpl.template_0, 20, renderOptsPattern);

    // TODO code cleanup
/*
    tmpl.rgroup_label = ['', '  Ketcher 12131120282D 1   1.00000     0.00000     0', '',
        '  1  0  0     0  0            999 V2000',
        '    0.0000    0.0000    0.0000 R#  0  0  0  0  0  0        0  0  0',
        'M  END'
    ];
    tmpl.rgroup_attpoints = ['', '  Ketcher 12131120282D 1   1.00000     0.00000     0', '',
        '  1  0  0  0  0  0  0  0  0  0999 V2000',
        '    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0',
        'M  APO  1   1   1',
        'M  END'
    ];
    ketcher.button_areas.rgroup_label = ketcher.showMolfileOpts('rgroup', tmpl.rgroup_label, 75, renderOpts);
*/

    ui.init(settings);
};

ketcher.getSmiles = function ()
{
    var saver = new chem.SmilesSaver();
    return saver.saveMolecule(ui.ctab, true);
};

ketcher.getMolfile = function ()
{
    var saver = new chem.MolfileSaver();
    return saver.saveMolecule(ui.ctab, true);
};

ketcher.setMolecule = function (mol_string)
{
    if (!Object.isString(mol_string))
        return;

    ui.loadMolecule(mol_string);
};

ketcher.addFragment = function (mol_string)
{
    if (!Object.isString(mol_string))
        return;

    ui.loadMolecule(mol_string, undefined, undefined, true);
};

ketcher.showMolfile = function (clientArea, molfileText, autoScale, hideImplicitHydrogen)
{
	return ketcher.showMolfileOpts(clientArea, molfileText, 75, {
        'showSelectionRegions':false,
        'showBondIds':false,
        'showHalfBondIds':false,
        'showLoopIds':false,
        'showAtomIds':false,
		'autoScale':autoScale||false,
		'autoScaleMargin':20,
		'hideImplicitHydrogen':hideImplicitHydrogen||false
    });
};

ketcher.showMolfileOpts = function (clientArea, molfileText, bondLength, opts)
{
    this.render = new rnd.Render(clientArea, bondLength, opts);
    if (molfileText)
        this.render.setMolecule(chem.Molfile.parseCTFile(typeof(molfileText)=='string' ? molfileText.split('\n') : molfileText));
    this.render.update();
    return this.render;
};

/*
ketcher.testShiftRayBox = function (clientArea)
{
    var bx = 50, by = 180, bw = 220, bh = 40;

    var b = new util.Box2Abs(bx, by, bx + bw, by + bh);
    var c = new Raphael(clientArea);

    c.rect(0, 0, 300, 300).attr({stroke:'#0f0'});
    c.rect(bx, by, bw, bh).attr({stroke:'#00f'});

    for (var i = 0; i < 3; ++i)
    {
        var p = new util.Vec2(Math.random() * 300, Math.random() * 300);
        var d = new util.Vec2(Math.random() * 300, Math.random() * 300);
        var p1 = p.add(d);
        c.circle(p.x, p.y, 4).attr({fill:'#0f0'});
        c.path("M{0},{1}L{2},{3}", p.x, p.y, p1.x, p1.y).attr({'stroke-width':'3','stroke':'#f00'});
        var t = Math.max(0, util.Vec2.shiftRayBox(p, d, b));
        var p0 = p.addScaled(d, t / d.length());
        c.path("M{0},{1}L{2},{3}", p0.x, p0.y, p1.x, p1.y).attr({'stroke-width':'1','stroke':'#000'});
    }
}

ketcher.runTest = function(test, context) {
    var runs = [], nr = 5, diff;

    for (var r = 0; r < nr; ++r) {
        var start = (new Date).getTime();
        for ( var n = 0; (diff = (new Date).getTime() - start) < 1000; n++ )
            test.call(context);
        runs.push( diff / n );
    }

    var avg = 0, std = 0, t;
    for (r = 0; r < nr; ++r) {
        t = runs[r];
        avg += t;
        std += t * t;
    }
    avg /= nr;
    std = Math.sqrt(std - avg * avg) / nr;
    console.log(avg + ' ' + std);
}
*/
jT.tools['ketcher'] = 
"" +
"<div class=\"ketcher_root\">" +
"<table id=\"ketcher_window\" tabindex=\"1\">" +
"<tr align=\"center\" id=\"main_toolbar\">" +
"<td style=\"width:36px\"><div style=\"position:relative\"><img class=\"sideButton modeButton stateButton\" id=\"selector\" selid=\"selector_lasso\" src=\"png/action/lasso.png\" alt=\"\" title=\"Select Tool (Esc)\" /><img class=\"dropdownButton\" id=\"selector_dropdown\" src=\"png/dropdown.png\" alt=\"\" /></div></td>" +
"<td class=\"toolDelimiter\"></td>" +
"<!--td style=\"width:36px\"><object type=\"image/svg+xml\" width=\"28\" height=\"28\" data=\"svg/document-new28x28.svg\"></object></td-->" +
"<td class=\"toolButtonCell toolButton\" id=\"new\"><img src=\"png/action/document-new.png\" alt=\"\" title=\"New (Ctrl+N)\" /></td>" +
"<td class=\"toolButtonCell toolButton\" id=\"open\"><img src=\"png/action/document-open.png\" alt=\"\" title=\"Open... (Ctrl+O)\" /></td>" +
"<td class=\"toolButtonCell toolButton\" id=\"save\"><img src=\"png/action/document-save-as.png\" alt=\"\" title=\"Save As... (Ctrl+S)\" /></td>" +
"<td class=\"toolDelimiter\"></td>" +
"<td class=\"toolButtonCell toolButton buttonDisabled\" id=\"undo\"><img src=\"png/action/edit-undo.png\" alt=\"\" title=\"Undo (Ctrl+Z)\" /></td>" +
"<td class=\"toolButtonCell toolButton buttonDisabled\" id=\"redo\"><img src=\"png/action/edit-redo.png\" alt=\"\" title=\"Redo (Ctrl+Shift+Z)\" /></td>" +
"<td class=\"toolButtonCell toolButton buttonDisabled\" id=\"cut\"><img src=\"png/action/edit-cut.png\" alt=\"\" title=\"Cut (Ctrl+X)\" /></td>" +
"<td class=\"toolButtonCell toolButton buttonDisabled\" id=\"copy\"><img src=\"png/action/edit-copy.png\" alt=\"\" title=\"Copy (Ctrl+C)\" /></td>" +
"<td class=\"toolButtonCell toolButton buttonDisabled\" id=\"paste\"><img src=\"png/action/edit-paste.png\" alt=\"\" title=\"Paste (Ctrl+V)\" /></td>" +
"<td class=\"toolDelimiter\"></td>" +
"<td class=\"toolButtonCell toolButton\" id=\"zoom_in\"><img src=\"png/action/view-zoom-in.png\" alt=\"\" title=\"Zoom In (+)\" /></td>" +
"<td class=\"toolButtonCell toolButton\" id=\"zoom_out\"><img src=\"png/action/view-zoom-out.png\" alt=\"\" title=\"Zoom Out (-)\" /></td>" +
"<td class=\"toolDelimiter\"></td>" +
"<td class=\"toolButtonCell toolButton serverRequired\" id=\"clean_up\"><img title=\"Clean Up (Ctrl+L)\" alt=\"\" src=\"png/action/layout.png\" /></td>" +
"<td class=\"toolButtonCell toolButton serverRequired\" id=\"aromatize\"><img title=\"Aromatize\" alt=\"\" src=\"png/action/arom.png\" /></td>" +
"<td class=\"toolButtonCell toolButton serverRequired\" id=\"dearomatize\"><img title=\"Dearomatize\" alt=\"\" src=\"png/action/dearom.png\" /></td>" +
"<td class=\"toolEmptyCell\" style=\"width:100%\"></td>" +
"<td style=\"width:1px\" rowspan=\"14\"></td>" +
"<td style=\"width:36px;padding:0 2px 0 0;\"><a href=\"http://www.ggasoftware.com/\" target=\"_blank\"><img src=\"png/logo.png\" alt=\"\" title=\"GGA Software Services\" /></a></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td><img class=\"sideButton modeButton\" id=\"select_erase\" src=\"png/action/edit-clear.png\" alt=\"\" title=\"Erase\" /></td>" +
"<td colspan=\"18\" rowspan=\"13\"><div id=\"client_area\"></div></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_h\" title=\"H Atom (H)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td><div style=\"position:relative\"><div class=\"sideButton modeButton stateButton\" id=\"bond\" selid=\"bond_single\" title=\"Single Bond (1)\"></div><img class=\"dropdownButton\" id=\"bond_dropdown\" src=\"png/dropdown.png\" alt=\"\" /></div></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_c\" title=\"C Atom (C)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td><div class=\"sideButton modeButton\" id=\"chain\" title=\"Chain Tool\"></div></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_n\" title=\"N Atom (N)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td><div style=\"position:relative\"><div class=\"sideButton modeButton stateButton\" id=\"template\" selid=\"template_0\" title=\"Benzene (T)\"></div><img class=\"dropdownButton\" id=\"template_dropdown\" src=\"png/dropdown.png\" alt=\"\" /></div></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_o\" title=\"O Atom (O)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td><div class=\"sideButton modeButton\" id=\"charge_plus\" title=\"Charge Plus (5)\"></div></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_s\" title=\"S Atom (S)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td><div class=\"sideButton modeButton\" id=\"charge_minus\" title=\"Charge Minus (5)\"></div></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_p\" title=\"P Atom (P)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td><div style=\"position:relative\"><img class=\"sideButton modeButton stateButton\" id=\"reaction\" selid=\"reaction_arrow\" src=\"png/action/reaction-arrow.png\" alt=\"\" title=\"Reaction Mapping Tool\" /><img class=\"dropdownButton\" id=\"reaction_dropdown\" src=\"png/dropdown.png\" alt=\"\" /></div></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_f\" title=\"F Atom (F)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td><img class=\"sideButton modeButton\" id=\"sgroup\" src=\"png/action/sgroup.png\" alt=\"\" title=\"S-Group (Ctrl+G)\" /></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_cl\" title=\"Cl Atom (Shift+C)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td><div style=\"position:relative\"><img class=\"sideButton modeButton stateButton\" id=\"rgroup\" selid=\"rgroup_label\" src=\"png/action/rgroup-label.png\" alt=\"\" title=\"R-Group Tool (Shift+R)\" /><img class=\"dropdownButton\" id=\"rgroup_dropdown\" src=\"png/dropdown.png\" alt=\"\" /></div></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_br\" title=\"Br Atom (Shift+B)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_i\" title=\"I Atom (I)\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_table\" title=\"Periodic table\"></div></td>" +
"</tr>" +
"<tr align=\"center\" class=\"sideButtonCell\">" +
"<td></td>" +
"<td><div class=\"sideButton modeButton\" id=\"atom_reagenerics\" title=\"Reaxys Generics\"></div></td>" +
"</tr>" +
"</table>" +
"" +
"<div class=\"dropdownList\" id=\"selector_dropdown_list\" style=\"display:none\">" +
"<table>" +
"" +
"<tr class=\"dropdownListItem\" id=\"selector_lasso\" title=\"Lasso Selection Tool\">" +
"<td><div id=\"select_lasso_todo\"><img src=\"png/action/lasso.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>Lasso selection tool</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"selector_square\" title=\"Rectangle Selection Tool\">" +
"<td><div id=\"select_square_todo\"><img src=\"png/action/rectangle.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>Rectangle selection tool</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"selector_fragment\" title=\"Fragment Selection Tool\">" +
"<td><div id=\"select_fragment\"><img src=\"png/action/structure.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>Fragment selection tool</td>" +
"</tr>" +
"</table>" +
"</div>" +
"" +
"<div class=\"dropdownList renderFirst\" id=\"bond_dropdown_list\" style=\"display:none\">" +
"<table>" +
"<tr class=\"dropdownListItem\" id=\"bond_single\" title=\"Single Bond (1)\">" +
"<td><div id=\"bond_single_preview\"></div>Single</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_double\" title=\"Double Bond (2)\">" +
"<td><div id=\"bond_double_preview\"></div>Double</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_triple\" title=\"Triple Bond (3)\">" +
"<td><div id=\"bond_triple_preview\"></div>Triple</td>" +
"</tr>" +
"<tr>" +
"<td class=\"dropdownListDelimiter\"></td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_up\" title=\"Single Up Bond (1)\">" +
"<td><div id=\"bond_up_preview\"></div>Single Up</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_down\" title=\"Single Down Bond (1)\">" +
"<td><div id=\"bond_down_preview\"></div>Single Down</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_updown\" title=\"Single Up/Down Bond (1)\">" +
"<td><div id=\"bond_updown_preview\"></div>Single Up/Down</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_crossed\" title=\"Double Cis/Trans Bond (2)\">" +
"<td><div id=\"bond_crossed_preview\"></div>Double Cis/Trans</td>" +
"</tr>" +
"<tr>" +
"<td class=\"dropdownListDelimiter\"></td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_any\" title=\"Any Bond (0)\">" +
"<td><div id=\"bond_any_preview\"></div>Any</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_aromatic\" title=\"Aromatic Bond (4)\">" +
"<td><div id=\"bond_aromatic_preview\"></div>Aromatic</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_singledouble\" title=\"Single/Double Bond\">" +
"<td><div id=\"bond_singledouble_preview\"></div>Single/Double</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_singlearomatic\" title=\"Single/Aromatic Bond\">" +
"<td><div id=\"bond_singlearomatic_preview\"></div>Single/Aromatic</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"bond_doublearomatic\" title=\"Double/Aromatic Bond\">" +
"<td><div id=\"bond_doublearomatic_preview\"></div>Double/Aromatic</td>" +
"</tr>" +
"</table>" +
"</div>" +
"" +
"<div class=\"dropdownList renderFirst\" id=\"template_dropdown_list\" style=\"display:none\">" +
"<table>" +
"<tr class=\"dropdownListItem\" id=\"template_0\" title=\"Benzene (T)\">" +
"<td><div id=\"template_0_preview\"></div>Benzene</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"template_1\" title=\"Cyclopentadiene (T)\">" +
"<td><div id=\"template_1_preview\"></div>Cyclopentadiene</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"template_2\" title=\"Cyclohexane (T)\">" +
"<td><div id=\"template_2_preview\"></div>Cyclohexane</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"template_3\" title=\"Cyclopentane (T)\">" +
"<td><div id=\"template_3_preview\"></div>Cyclopentane</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"template_4\" title=\"Cyclopropane (T)\">" +
"<td><div id=\"template_4_preview\"></div>Cyclopropane</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"template_5\" title=\"Cyclobutane (T)\">" +
"<td><div id=\"template_5_preview\"></div>Cyclobutane</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"template_6\" title=\"Cycloheptane (T)\">" +
"<td><div id=\"template_6_preview\"></div>Cycloheptane</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"template_7\" title=\"Cyclooctane (T)\">" +
"<td><div id=\"template_7_preview\"></div>Cyclooctane</td>" +
"</tr>" +
"</table>" +
"</div>" +
"" +
"<div class=\"dropdownList\" id=\"reaction_dropdown_list\" style=\"display:none\">" +
"<table>" +
"<tr class=\"dropdownListItem\" id=\"reaction_arrow\" title=\"Reaction Arrow Tool\">" +
"<td><div id=\"reaction_arrow_todo\"><img src=\"png/action/reaction-arrow.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>Reaction Arrow Tool</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"reaction_plus\" title=\"Reaction Plus Tool\">" +
"<td><div id=\"reaction_plus_todo\"><img src=\"png/action/reaction-plus.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>Reaction Plus Tool</td>" +
"</tr>" +
"<tr class=\"dropdownListItem serverRequired\" id=\"reaction_automap\" title=\"Reaction Mapping Tool\">" +
"<td><div id=\"reaction_automap_todo\"><img src=\"png/action/reaction-automap.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>Reaction Auto-Mapping</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"reaction_map\" title=\"Reaction Mapping Tool\">" +
"<td><div id=\"reaction_map_todo\"><img src=\"png/action/reaction-map.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>Reaction Mapping Tool</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"reaction_unmap\" title=\"Reaction Unmappping Tool\">" +
"<td><div id=\"reaction_unmap_todo\"><img src=\"png/action/reaction-unmap.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>Reaction Unmapping Tool</td>" +
"</tr>" +
"</table>" +
"</div>" +
"" +
"<div class=\"dropdownList\" id=\"rgroup_dropdown_list\" style=\"display:none\">" +
"<table>" +
"<tr class=\"dropdownListItem\" id=\"rgroup_label\" title=\"R-Group Label Tool (Shift+R)\">" +
"<td><div id=\"rgroup_label_todo\"><img src=\"png/action/rgroup-label.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>R-Group Label Tool</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"rgroup_fragment\" title=\"R-Group Fragment Tool (Shift+R)\">" +
"<td><div id=\"rgroup_fragment_todo\"><img src=\"png/action/rgroup-fragment.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>R-Group Fragment Tool</td>" +
"</tr>" +
"<tr class=\"dropdownListItem\" id=\"rgroup_attpoints\" title=\"Attachment Point Tool (Shift+R)\">" +
"<td><div id=\"rgroup_attpoints_todo\"><img src=\"png/action/rgroup-attpoints.png\" alt=\"\" style=\"width:24px;height:24px;\" /></div>Attachment Point Tool</td>" +
"</tr>" +
"</table>" +
"</div>" +
"" +
"<input id=\"input_label\" type=\"text\" maxlength=\"4\" size=\"4\" style=\"display:none;\" />" +
"" +
"<div id=\"window_cover\" style=\"width:0;height:0;display:none;\"><div id=\"loading\" style=\"display:none;\"></div></div>" +
"" +
"<div class=\"dialogWindow fileDialog\" id=\"open_file\" style=\"display:none;\">" +
"<div style=\"width:100%\">" +
"<div>" +
"Open File" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<div class=\"serverRequired\" style=\"font-size:small\">" +
"<input type=\"radio\" id=\"radio_open_from_input\" name=\"input_source\" checked>Input</input>" +
"<input type=\"radio\" id=\"radio_open_from_file\" name=\"input_source\">File</input>" +
"</div>" +
"<div class=\"serverRequired\" style=\"font-size:small\" align=\"left\">" +
"<input type=\"checkbox\" id=\"checkbox_open_copy\" name=\"open_mode\">Load as a fragment and copy to the Clipboard</input>" +
"</div>" +
"<div class=\"serverRequired\" id=\"open_from_file\">" +
"<form id=\"upload_mol\" style=\"margin-top:4px\" action=\"open\" enctype=\"multipart/form-data\" target=\"buffer_frame\" method=\"post\">" +
"<input type=\"file\" name=\"filedata\" id=\"molfile_path\" />" +
"<div style=\"margin-top:0.5em;text-align:center\">" +
"<input id=\"upload_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input type=\"submit\" value=\"OK\" />" +
"</div>" +
"</form>" +
"</div>" +
"<div style=\"margin:4px;\" id=\"open_from_input\">" +
"<textarea class=\"chemicalText\" id=\"input_mol\" wrap=\"off\"></textarea>" +
"<div style=\"margin-top:0.5em;text-align:center\">" +
"<input id=\"read_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"read_ok\" type=\"submit\" value=\"OK\" />" +
"</div>" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow fileDialog\" id=\"save_file\" style=\"display:none;\">" +
"<div style=\"width:100%\">" +
"<div>" +
"Save File" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<div>" +
"<label>Format:</label>" +
"<select id=\"file_format\">Format:" +
"<option value=\"mol\">MDL/Symyx Molfile</option>" +
"<option value=\"smi\">Daylight SMILES</option>" +
"<!--option value=\"png\">Portable Network Graphics PNG</option>" +
"<option value=\"svg\">Scalable Vector Graphics SVG</option-->" +
"</select>" +
"</div>" +
"<div style=\"margin:4px;\">" +
"<textarea class=\"chemicalText\" id=\"output_mol\" wrap=\"off\" readonly></textarea>" +
"<form  id=\"download_mol\" style=\"margin-top:0.5em;text-align:center\" action=\"save\" enctype=\"multipart/form-data\" target=\"_self\" method=\"post\">" +
"<input type=\"hidden\" id=\"mol_data\" name=\"filedata\" />" +
"<input type=\"submit\" class=\"serverRequired\" value=\"Save...\" />" +
"<input id=\"save_ok\" type=\"button\" value=\"Close\" />" +
"</form>" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow propDialog\" id=\"atom_properties\" style=\"display:none;\">" +
"<div style=\"width:100%\">" +
"<div>" +
"Atom Properties" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<table style=\"text-align:left\">" +
"<tr>" +
"<td>" +
"<label>Label:</label>" +
"</td>" +
"<td>" +
"<input id=\"atom_label\" type=\"text\" maxlength=\"2\" size=\"3\" />" +
"</td>" +
"<td rowspan=\"5\" style=\"width:5px\">" +
"</td>" +
"<td>" +
"<label>Number:</label>" +
"</td>" +
"<td>" +
"<input id=\"atom_number\" type=\"text\" readonly=\"readonly\" maxlength=\"3\" size=\"3\" />" +
"</td>" +
"<td rowspan=\"5\" style=\"width:10px\">" +
"</td>" +
"<td colspan=\"2\" style=\"background-color: #D7D7D7\">" +
"Query specific" +
"</td>" +
"</tr>" +
"<tr>" +
"<td>" +
"<label>Charge:</label>" +
"</td>" +
"<td>" +
"<input id=\"atom_charge\" type=\"text\" maxlength=\"3\" size=\"3\" />" +
"</td>" +
"<td>" +
"<label>Valency:</label>" +
"</td>" +
"<td>" +
"<select id=\"atom_valence\" style=\"width:100%\">" +
"<option value=\"\"></option>" +
"<option value=\"0\">0</option>" +
"<option value=\"1\">I</option>" +
"<option value=\"2\">II</option>" +
"<option value=\"3\">III</option>" +
"<option value=\"4\">IV</option>" +
"<option value=\"5\">V</option>" +
"<option value=\"6\">VI</option>" +
"<option value=\"7\">VII</option>" +
"<option value=\"8\">VIII</option>" +
"</select>" +
"</td>" +
"<td>" +
"<label>Ring bond count:</label>" +
"</td>" +
"<td>" +
"<select id=\"atom_ringcount\" style=\"width:100%\">" +
"<option value=\"0\"></option>" +
"<option value=\"-2\">As drawn</option>" +
"<option value=\"-1\">0</option>" +
"<option value=\"2\">2</option>" +
"<option value=\"3\">3</option>" +
"<option value=\"4\">4</option>" +
"</select>" +
"</td>" +
"</tr>" +
"<tr>" +
"<td>" +
"<label>Isotope:</label>" +
"</td>" +
"<td>" +
"<input id=\"atom_isotope\" type=\"text\" maxlength=\"3\" size=\"3\" />" +
"</td>" +
"<td>" +
"<label>Radical:</label>" +
"</td>" +
"<td>" +
"<select id=\"atom_radical\">" +
"<option value=\"0\"></option>" +
"<option value=\"2\">Monoradical</option>" +
"<option value=\"1\">Diradical (singlet)</option>" +
"<option value=\"3\">Diradical (triplet)</option>" +
"</select>" +
"</td>" +
"<td>" +
"<label>H count:</label>" +
"</td>" +
"<td>" +
"<select id=\"atom_hcount\" style=\"width:100%\">" +
"<option value=\"0\"></option>" +
"<option value=\"1\">0</option>" +
"<option value=\"2\">1</option>" +
"<option value=\"3\">2</option>" +
"<option value=\"4\">3</option>" +
"<option value=\"5\">4</option>" +
"</select>" +
"</td>" +
"</tr>" +
"<tr>" +
"<td colspan=\"5\" style=\"background-color: #D7D7D7\">" +
"Reaction flags" +
"</td>" +
"<td>" +
"<label>Substitution count:</label>" +
"</td>" +
"<td>" +
"<select id=\"atom_substitution\" style=\"width:100%\">" +
"<option value=\"0\"></option>" +
"<option value=\"-2\">As drawn</option>" +
"<option value=\"-1\">0</option>" +
"<option value=\"1\">1</option>" +
"<option value=\"2\">2</option>" +
"<option value=\"3\">3</option>" +
"<option value=\"4\">4</option>" +
"<option value=\"5\">5</option>" +
"<option value=\"6\">6</option>" +
"</select>" +
"</td>" +
"</tr>" +
"<tr>" +
"<td>" +
"<label>Inversion:</label>" +
"</td>" +
"<td>" +
"<select id=\"atom_inversion\">" +
"<option value=\"0\"></option>" +
"<option value=\"1\">Inverts</option>" +
"<option value=\"2\">Retains</option>" +
"</select>" +
"</td>" +
"<td>" +
"<label>Exact:</label>" +
"</td>" +
"<td>" +
"<select id=\"atom_exactchange\" style=\"width:100%\">" +
"<option value=\"0\"></option>" +
"<option value=\"1\">Exact change</option>" +
"</select>" +
"</td>" +
"<td>" +
"<label>Unsaturation:</label>" +
"</td>" +
"<td>" +
"<select id=\"atom_unsaturation\">" +
"<option value=\"0\"></option>" +
"<option value=\"1\">Unsaturated</option>" +
"</select>" +
"</td>" +
"</tr>" +
"</table>" +
"<div style=\"margin-top:0.5em\">" +
"<input id=\"atom_prop_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"atom_prop_ok\" type=\"button\" value=\"OK\" />" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow propDialog\" id=\"atom_attpoints\" style=\"display:none;\">" +
"<div style=\"width:100%\">" +
"<div>" +
"Attachment Points" +
"</div>" +
"<table style=\"text-align:left\">" +
"<tr>" +
"<td>" +
"<input type=\"checkbox\" id=\"atom_ap1\">" +
"</td>" +
"<td>" +
"Primary attachment point" +
"</td>" +
"</tr>" +
"<tr>" +
"<td>" +
"<input type=\"checkbox\" id=\"atom_ap2\">" +
"</td>" +
"<td>" +
"Secondary attachment point" +
"</td>" +
"</tr>" +
"</table>" +
"<div style=\"height:0.5em\"></div>" +
"<div style=\"margin-top:0.5em\">" +
"<input id=\"atom_attpoints_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"atom_attpoints_ok\" type=\"button\" value=\"OK\" />" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow propDialog\" id=\"bond_properties\" style=\"display:none;\">" +
"<div style=\"width:100%\">" +
"<div>" +
"Bond Properties" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<table style=\"text-align:left\">" +
"<tr>" +
"<td>" +
"<label>Type:</label>" +
"</td>" +
"<td>" +
"<select id=\"bond_type\" style=\"width:100%\">" +
"<option value=\"single\">Single</option>" +
"<option value=\"up\">Single Up</option>" +
"<option value=\"down\">Single Down</option>" +
"<option value=\"updown\">Single Up/Down</option>" +
"<option value=\"double\">Double</option>" +
"<option value=\"crossed\">Double Cis/Trans</option>" +
"<option value=\"triple\">Triple</option>" +
"<option value=\"aromatic\">Aromatic</option>" +
"<option value=\"any\">Any</option>" +
"<option value=\"singledouble\">Single/Double</option>" +
"<option value=\"singlearomatic\">Single/Aromatic</option>" +
"<option value=\"doublearomatic\">Double/Aromatic</option>" +
"</select>" +
"</td>" +
"</tr>" +
"<tr>" +
"<td>" +
"<label>Topology:</label>" +
"</td>" +
"<td>" +
"<select id=\"bond_topology\" style=\"width:100%\">" +
"<option value=\"0\">Either</option>" +
"<option value=\"1\">Ring</option>" +
"<option value=\"2\">Chain</option>" +
"</select>" +
"</td>" +
"</tr>" +
"<tr>" +
"<td>" +
"<label>Reacting Center:</label>" +
"</td>" +
"<td>" +
"<select id=\"bond_center\">" +
"<option value=\"0\">Unmarked</option>" +
"<option value=\"-1\">Not center</option>" +
"<option value=\"1\">Center</option>" +
"<option value=\"2\">No change</option>" +
"<option value=\"4\">Made/broken</option>" +
"<option value=\"8\">Order changes</option>" +
"<option value=\"12\">Made/broken and changes</option>" +
"<!--option value=\"5\">Order changes</option>" +
"<option value=\"9\">Order changes</option>" +
"<option value=\"13\">Order changes</option-->" +
"</select>" +
"</td>" +
"</tr>" +
"</table>" +
"<div style=\"margin-top:0.5em\">" +
"<input id=\"bond_prop_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"bond_prop_ok\" type=\"button\" value=\"OK\" />" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow sgroupDialog\" id=\"sgroup_properties\" style=\"display:none;\">" +
"<div style=\"width:100%\">" +
"<div>" +
"S-Group Properties" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<table style=\"text-align:left\">" +
"<tr>" +
"<td>" +
"<label>Type:</label>" +
"</td>" +
"<td>" +
"<select id=\"sgroup_type\">" +
"<option value=\"GEN\">Generic</option>" +
"<option value=\"MUL\">Multiple group</option>" +
"<option value=\"SRU\">SRU polymer</option>" +
"<option value=\"SUP\">Superatom</option>" +
"<option value=\"DAT\">Data</option>" +
"</select>" +
"</td>" +
"</tr>" +
"<tr class=\"generalSGroup\">" +
"<td>" +
"<label>Connection:</label>" +
"</td>" +
"<td>" +
"<select id=\"sgroup_connection\">" +
"<option value=\"ht\">Head-to-tail</option>" +
"<option value=\"hh\">Head-to-head</option>" +
"<option value=\"eu\">Either unknown</option>" +
"</select>" +
"</td>" +
"</tr>" +
"<tr class=\"generalSGroup\">" +
"<td>" +
"<label>Label (subscript):</label>" +
"</td>" +
"<td>" +
"<input id=\"sgroup_label\" type=\"text\" maxlength=\"15\" size=\"15\" />" +
"</td>" +
"</tr>" +
"<tr class=\"dataSGroup\">" +
"<td>" +
"<label>Field name:</label>" +
"</td>" +
"<td>" +
"<input id=\"sgroup_field_name\" type=\"text\" maxlength=\"30\" size=\"30\" />" +
"</td>" +
"</tr>" +
"<tr class=\"dataSGroup\">" +
"<td>" +
"<label>Field value:</label>" +
"</td>" +
"</tr>" +
"<tr class=\"dataSGroup\">" +
"<td colspan=\"2\">" +
"<textarea class=\"dataSGroupValue\" id=\"sgroup_field_value\"></textarea>" +
"</td>" +
"</tr>" +
"<tr class=\"dataSGroup\">" +
"<td colspan=\"2\">" +
"<input type=\"radio\" id=\"sgroup_pos_absolute\" name=\"data_field_pos\" checked>Absolute</input>" +
"<input type=\"radio\" id=\"sgroup_pos_relative\" name=\"data_field_pos\">Relative</input>" +
"<input type=\"radio\" id=\"sgroup_pos_attached\" name=\"data_field_pos\">Attached</input>" +
"</td>" +
"</tr>" +
"</table>" +
"<div style=\"margin-top:0.5em\">" +
"<input id=\"sgroup_prop_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"sgroup_prop_ok\" type=\"button\" value=\"OK\" />" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow automapDialog\" id=\"automap_properties\" style=\"display:none;\">" +
"<div style=\"width:100%\">" +
"<div>" +
"Reaction Auto-Mapping Parameter" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<table style=\"text-align:left\">" +
"<tr>" +
"<td>" +
"<label>Mode:</label>" +
"</td>" +
"<td>" +
"<select id=\"automap_mode\">" +
"<option value=\"discard\">Discard</option>" +
"<option value=\"keep\">Keep</option>" +
"<option value=\"alter\">Alter</option>" +
"<option value=\"clear\">Clear</option>" +
"</select>" +
"</td>" +
"</tr>" +
"</table>" +
"<div style=\"margin-top:0.5em\">" +
"<input id=\"automap_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"automap_ok\" type=\"button\" value=\"OK\" />" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow elemTableDialog\" id=\"elem_table\" style=\"display:none;\">" +
"<div>" +
"<div>" +
"Periodic table" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<div id=\"elem_table_area\"></div>" +
"<div align=\"left\">" +
"<input type=\"radio\" id=\"elem_table_single\" name=\"atom_list\"" +
"onchange=\"if (!Prototype.Browser.IE) ui.elem_table_obj.setMode('single')\"" +
"onclick=\"if (Prototype.Browser.IE) ui.elem_table_obj.setMode('single')\"" +
">Single</input> <br />" +
"<input type=\"radio\" id=\"elem_table_list\" name=\"atom_list\"" +
"onchange=\"if (!Prototype.Browser.IE) ui.elem_table_obj.setMode('list')\"" +
"onclick=\"if (Prototype.Browser.IE) ui.elem_table_obj.setMode('list')\"" +
">List</input> <br />" +
"<input type=\"radio\" id=\"elem_table_notlist\" name=\"atom_list\"" +
"onchange=\"if (!Prototype.Browser.IE) ui.elem_table_obj.setMode('notlist')\"" +
"onclick=\"if (Prototype.Browser.IE) ui.elem_table_obj.setMode('notlist')\"" +
">Not List</input>" +
"</div>" +
"<div style=\"margin-top:0.5em\">" +
"<input id=\"elem_table_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"elem_table_ok\" type=\"button\" value=\"OK\" />" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow rgroupTableDialog\" id=\"rgroup_table\" style=\"display:none;\">" +
"<div>" +
"<div>" +
"R-Group" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<div id=\"rgroup_table_area\"></div>" +
"<div style=\"margin-top:0.5em\">" +
"<input id=\"rgroup_table_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"rgroup_table_ok\" type=\"button\" value=\"OK\" />" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow rlogicTableDialog\" id=\"rlogic_table\" style=\"display:none;\">" +
"<div style=\"width:100%\">" +
"<div>" +
"R-Group Logic" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<table style=\"text-align:left\">" +
"<tr>" +
"<td>" +
"<label for=\"rlogic_occurrence\">Occurrence:</label>" +
"<input id=\"rlogic_occurrence\" type=\"text\" maxlength=\"50\" size=\"10\" />" +
"</td>" +
"<td>" +
"<label for=\"rlogic_resth\">RestH:</label>" +
"<select id=\"rlogic_resth\">" +
"<option value=\"0\">Off</option>" +
"<option value=\"1\">On</option>" +
"</select>" +
"</td>" +
"<td>" +
"<label for=\"rlogic_if\">Condition:</label>" +
"<select id=\"rlogic_if\">" +
"<option value=\"0\">Always</option>" +
"</select>" +
"</td>" +
"</tr>" +
"</table>" +
"<div style=\"margin-top:0.5em\">" +
"<input id=\"rlogic_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"rlogic_ok\" type=\"button\" value=\"OK\" />" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<div class=\"dialogWindow reagenericsTableDialog\" id=\"reagenerics_table\" style=\"display:none;\">" +
"<div>" +
"<div>" +
"Reaxys Generics" +
"</div>" +
"<div style=\"height:0.5em\"></div>" +
"<div id=\"reagenerics_table_area\"></div>" +
"<div style=\"margin-top:0.5em\">" +
"<input id=\"reagenerics_table_cancel\" type=\"button\" value=\"Cancel\" />" +
"<input id=\"reagenerics_table_ok\" type=\"button\" value=\"OK\" disabled />" +
"</div>" +
"</div>" +
"</div>" +
"" +
"<iframe name=\"buffer_frame\" id=\"buffer_frame\" src=\"about:blank\" style=\"display:none\">" +
"</iframe>" +
"</div>" +
"";

