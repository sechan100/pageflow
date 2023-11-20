"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
exports.__esModule = true;
exports.useCreateChapterStore = void 0;
var zustand_1 = require("zustand");
exports.useCreateChapterStore = zustand_1.create(function (set) { return ({
    isMutated: false,
    resetMutation: function () { return set(function (state) { return (__assign(__assign({}, state), { isMutated: false })); }); },
    requestCreateChapter: function () {
        set(function (state) { return ({
            isMutated: true
        }); });
    }
}); });
function NewChapterBtn(_a) {
    var localOutline = _a.outline;
    var requestCreateChapter = exports.useCreateChapterStore().requestCreateChapter;
    return (React.createElement("button", { type: "button", onClick: function () { return requestCreateChapter(); }, className: "fixed z-20 top-4 left-40 px-5 py-2 text-xs font-medium text-center ext-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700" }, "\uC0C8 \uCC55\uD130"));
}
exports["default"] = NewChapterBtn;
