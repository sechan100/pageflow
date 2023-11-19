"use strict";
exports.__esModule = true;
exports.ApiOnLoadingCache = void 0;
var react_1 = require("react");
// react query의 데이터가 invalidate되었을 때, 잠깐 동안 임시로 보여줄 stale한 데이터를 저장해두는 cache.
// 데이터가 처음 로딩중일 때는 어쩔 수 없이 fallback을 보여주지만, 데이터 업데이트 도중에도 fallback을 보여주게되면 데이터가 갑자기 깜빡이는 것처럼 보이거나, 데이터가 업데이트 되기까지 
// 책 데이터가 없어지는 것처럼 보이므로... 이 시간동안 보여줄 데이터를 캐싱한다.
exports.ApiOnLoadingCache = react_1.createContext({ outlineCache: null, setOutlineCache: function () { } });
