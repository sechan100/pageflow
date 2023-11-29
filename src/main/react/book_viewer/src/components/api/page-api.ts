
import { ILocation } from "../nav/PageCursor";
import { IPage } from "../../types/types";
import axios from "axios";
import { useQuery } from "react-query";






const fallBackPage : IPage = {
  id: 0,
  title: "페이지를 가져오지 못했습니다.",
  content: "페이지를 가져오지 못했습니다."
}


type IGetPageAsync = (location: ILocation) => IPage; 




export const useGetPage = (bookId : number, pageMap : Map<string, number>, location: ILocation) : IPage => {

  const pageId = getPageIdByLocation(pageMap, location);

  const { data, isLoading, isFetching } = useQuery<IPage | null>(
    ['page', pageId], // query key

    () => getPageAsync(bookId, pageId),  // query fn

    { // options
        enabled: bookId > 0 && pageId > 0, // bookId가 0보다 클 때만 요청을 보냅니다.
    })
    
  if(data && !isLoading && !isFetching){
    return data;
  } else {
    return fallBackPage;
  }
}



  const getPageAsync = async (bookId : number, pageId : number) => {
    
    const response = await axios.get(`/api/books/${bookId}/chapters/pages/${pageId}`)

    if(response){
      console.log("### page fetch ajax success! ###", response.data);
      return response.data as IPage;
    } else {
      return null;
    }
  }



function getPageIdByLocation(pageMap : Map<string, number>, location : ILocation) : number {
  const location_str = `${location.chapterIdx},${location.pageIdx}`;
  
  const pageId = pageMap.get(location_str);
  if(pageId){
    return pageId;
  } else {
    return 0;
  }
  
}