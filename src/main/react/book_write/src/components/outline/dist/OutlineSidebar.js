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
var BookBasicPage_1 = require("../outline/BookBasicPage");
var Chapter_1 = require("./Chapter");
var OutlineSidebarWrapper_1 = require("./OutlineSidebarWrapper");
var book_apis_1 = require("../../api/book-apis");
var react_beautiful_dnd_1 = require("react-beautiful-dnd");
exports.pageDropAreaPrefix = 'pageDropArea-';
exports.chapterDraggablePrefix = 'chapter-';
exports.pageDraggablePrefix = 'page-';
function OutlineSidebar(props) {
    var bookId = props.bookId, queryClient = props.queryClient, outlineBufferStatusReducer = props.outlineBufferStatusReducer;
    var outline = book_apis_1.useGetOutline(bookId);
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
    return (React.createElement(OutlineSidebarWrapper_1["default"], __assign({}, props),
        React.createElement(BookBasicPage_1["default"], { bookId: outline.id }),
        React.createElement(react_beautiful_dnd_1.Droppable, { droppableId: "chapter-outline", type: 'CHAPTER' }, function (provided) {
            var _a;
            return (React.createElement("div", __assign({ ref: provided.innerRef }, provided.droppableProps), (_a = outline.chapters) === null || _a === void 0 ? void 0 :
                _a.map(function (chapter, index) { return (React.createElement(react_beautiful_dnd_1.Draggable, { key: exports.chapterDraggablePrefix + chapter.id, draggableId: exports.chapterDraggablePrefix + chapter.id, index: index }, function (provided) { return (React.createElement("div", __assign({ ref: provided.innerRef }, provided.draggableProps, provided.dragHandleProps),
                    React.createElement(Chapter_1["default"], { chapter: chapter, chapterOpenStatus: chapterOpenStatus }))); })); }),
                provided.placeholder));
        })));
}
exports["default"] = OutlineSidebar;
