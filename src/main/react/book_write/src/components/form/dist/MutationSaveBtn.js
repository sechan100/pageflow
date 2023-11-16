"use strict";
exports.__esModule = true;
/* eslint-disable react-hooks/exhaustive-deps */
var react_1 = require("react");
function MutationSaveBtn(_a) {
    var setSaveActive = _a.setSaveActive, isUpdated = _a.isUpdated;
    var updateAlertTooltip = react_1.useRef(null);
    react_1.useEffect(function () {
        if (isUpdated.current) {
            updateAlertPingHandler();
        }
    }, [isUpdated.current]);
    function saveActiveHandler() {
        if (isUpdated.current) {
            setSaveActive(function (prevSaveActive) { return !prevSaveActive; });
        }
    }
    return (React.createElement("div", { onClick: saveActiveHandler, className: "flex justify-start fixed z-50 right-7 top-7" },
        isUpdated.current &&
            React.createElement("div", { className: "relative flex items-center mb-2 mr-3 transition-opacity duration-[1500ms] opacity-0", ref: updateAlertTooltip },
                React.createElement("div", { className: "tooltip bg-white text-black border border-gray-300 py-1 px-2 rounded shadow-lg" },
                    "\uBCC0\uACBD\uC0AC\uD56D\uC774 \uC788\uC2B5\uB2C8\uB2E4",
                    React.createElement("div", { className: "tooltip-arrow absolute top-[40%] right-1 w-0 h-0 border-transparent border-solid border-l-2 border-t-2 border-b-2 transform -translate-y-1/2 -translate-x-1/2" }))),
        React.createElement("div", { className: (isUpdated.current ? "bg-gray-700 hover:bg-gray-900" : "bg-gray-500") + " w-12 h-12 p-3 mb-3 rounded-full cursor-pointer" },
            isUpdated.current &&
                React.createElement("span", { className: "absolute top-1 right-[1px] flex h-3 w-3" },
                    React.createElement("span", { className: "animate-ping absolute inline-flex h-full w-full rounded-full bg-sky-400 opacity-75" }),
                    React.createElement("span", { className: "relative inline-flex rounded-full h-3 w-3 bg-sky-500" })),
            React.createElement("svg", { className: "w-6 h-6 text-gray-800 dark:text-white", "aria-hidden": "true", xmlns: "http://www.w3.org/2000/svg", fill: "none", viewBox: "0 0 16 18" },
                React.createElement("path", { stroke: "currentColor", strokeLinecap: "round", strokeLinejoin: "round", strokeWidth: "1.5", d: "M8 1v11m0 0 4-4m-4 4L4 8m11 4v3a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2v-3" })))));
    function updateAlertPingHandler() {
        setTimeout(function () {
            if (updateAlertTooltip.current) {
                updateAlertTooltip.current.classList.remove("opacity-0");
            }
        }, 100);
    }
}
exports["default"] = MutationSaveBtn;
