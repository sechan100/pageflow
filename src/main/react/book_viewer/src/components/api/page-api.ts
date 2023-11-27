import { usePagesStore } from "../../App";
import { ILocation } from "../nav/PageCursor";
import { IPage } from "../../types/types";






export const useGetPage = (bookId : number, pageMap : Map<string, number>) : (location: ILocation) => IPage => {
  const getPage = usePagesStore(state => state.dummyPagesStore?.getPage);


  const getPageAsync = ({ chapterIdx, pageIdx }: ILocation): IPage => {
    const pageId = pageMap.get(`${chapterIdx},${pageIdx}`) || 0;
    const page = getPage ? getPage(pageId) : null;
    return page ? page : { id: 0, title: "", content: "Page not found" };
  }

  return getPageAsync;
}
