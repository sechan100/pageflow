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
exports.pageDraggablePrefix = exports.chapterDraggablePrefix = exports.pageDropAreaPrefix = void 0;
var react_1 = require("react");
var BookBasicPage_1 = require("./items/BookBasicPage");
var Chapter_1 = require("./items/Chapter");
var OutlineSidebar_1 = require("./OutlineSidebar");
var react_beautiful_dnd_1 = require("react-beautiful-dnd");
exports.pageDropAreaPrefix = 'pageDropArea-';
exports.chapterDraggablePrefix = 'chapter-';
exports.pageDraggablePrefix = 'page-';
function OutlineContext(_a) {
    var localOutline = _a.outline;
    var openedChapterIds = react_1.useRef([]);
    function addOpenedChapterIds(chapterId) {
        openedChapterIds.current.push(chapterId);
    }
    function removeOpenedChapterIds(chapterId) {
        var index = openedChapterIds.current.indexOf(chapterId);
        if (index > -1) {
            openedChapterIds.current.splice(index, 1);
        }
    }
    var chapterOpenStatus = {
        openedChapterIds: openedChapterIds,
        addOpenedChapterIds: addOpenedChapterIds,
        removeOpenedChapterIds: removeOpenedChapterIds
    };
    return (React.createElement(OutlineSidebar_1["default"], { outline: localOutline },
        React.createElement(BookBasicPage_1["default"], { outline: localOutline }),
        React.createElement(react_beautiful_dnd_1.Droppable, { droppableId: "chapter-outline", type: 'CHAPTER' }, function (provided) {
            var _a;
            return (React.createElement("div", __assign({ ref: provided.innerRef }, provided.droppableProps), (_a = localOutline.chapters) === null || _a === void 0 ? void 0 :
                _a.map(function (chapter, index) { return (React.createElement(react_beautiful_dnd_1.Draggable, { key: exports.chapterDraggablePrefix + chapter.id, draggableId: exports.chapterDraggablePrefix + chapter.id, index: index }, function (provided) { return (React.createElement("div", __assign({ ref: provided.innerRef }, provided.draggableProps, provided.dragHandleProps),
                    React.createElement(Chapter_1["default"], { chapter: chapter, chapterOpenStatus: chapterOpenStatus }))); })); }),
                provided.placeholder));
        })));
}
exports["default"] = OutlineContext;
