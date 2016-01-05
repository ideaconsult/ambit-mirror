/*
 * Polyfill for Object.__defineGetter__ and Object.__defineSetter__ in IE9
 * http://stackoverflow.com/a/27400162
 * Modified to make the methods non-enumerable
 */

var proto = Object.prototype;

if( !proto.__defineGetter__) {
    Object.defineProperty(proto, '__defineGetter__', {
        enumerable: false,
        value: function(name, func) {
            var descriptor = Object.getOwnPropertyDescriptor(this, name);
            var new_descriptor = { get: func, configurable: true, enumerable: false};
            if (descriptor) {
                console.assert(descriptor.configurable, "Cannot set getter");
                if (descriptor.set) new_descriptor.set = descriptor.set; // COPY OLD SETTER
            }
            Object.defineProperty(this, name, new_descriptor);
        }
    });
}

if( !proto.__defineSetter__) {
    Object.defineProperty(proto, '__defineSetter__', {
        enumerable: false,
        value: function(name, func) {
            var descriptor = Object.getOwnPropertyDescriptor(this, name);
            var new_descriptor = { set: func, configurable: true, enumerable: false};
            if (descriptor) {
                console.assert(descriptor.configurable, "Cannot set setter");
                if (descriptor.get) new_descriptor.get = descriptor.get; // COPY OLD GETTER
            }
            Object.defineProperty(this, name, new_descriptor);
        }
    });
}

/*
 * Polyfil for ArrayBuffer.slice() method in IE10
 * http://stackoverflow.com/a/21440217
 */
if (!ArrayBuffer.prototype.slice) {
    //Returns a new ArrayBuffer whose contents are a copy of this ArrayBuffer's
    //bytes from `begin`, inclusive, up to `end`, exclusive
    ArrayBuffer.prototype.slice = function (begin, end) {
        //If `begin` is unspecified, Chrome assumes 0, so we do the same
        if (begin === void 0) {
            begin = 0;
        }

        //If `end` is unspecified, the new ArrayBuffer contains all
        //bytes from `begin` to the end of this ArrayBuffer.
        if (end === void 0) {
            end = this.byteLength;
        }

        //Chrome converts the values to integers via flooring
        begin = Math.floor(begin);
        end = Math.floor(end);

        //If either `begin` or `end` is negative, it refers to an
        //index from the end of the array, as opposed to from the beginning.
        if (begin < 0) {
            begin += this.byteLength;
        }
        if (end < 0) {
            end += this.byteLength;
        }

        //The range specified by the `begin` and `end` values is clamped to the
        //valid index range for the current array.
        begin = Math.min(Math.max(0, begin), this.byteLength);
        end = Math.min(Math.max(0, end), this.byteLength);

        //If the computed length of the new ArrayBuffer would be negative, it
        //is clamped to zero.
        if (end - begin <= 0) {
            return new ArrayBuffer(0);
        }

        var result = new ArrayBuffer(end - begin);
        var resultBytes = new Uint8Array(result);
        var sourceBytes = new Uint8Array(this, begin, end - begin);

        resultBytes.set(sourceBytes);

        return result;
    };
}
