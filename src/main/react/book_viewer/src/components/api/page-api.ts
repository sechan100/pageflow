import { dummyPagesStore } from "../../App";
import { ILocation } from "../nav/PageCursor";
import { IPage } from "../../types/types";






export const useGetPage = (bookId : number, pageMap : Map<string, number>) : (location: ILocation) => IPage => {
  const { getPage } = dummyPagesStore();


  const getPageAsync = ({chapterIdx, pageIdx} : ILocation) : IPage => {
    const pageId = pageMap.get(`${chapterIdx},${pageIdx}`) || 0;
    return getPage(pageId) ? getPage(pageId) as IPage : {id: 0, title:"", content: "Page not found"};
  }

  return getPageAsync;
}
