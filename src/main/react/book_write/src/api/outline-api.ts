import axios, { AxiosResponse } from "axios";
import { UseMutateAsyncFunction, useMutation, useQuery } from "react-query";
import { Outline } from "../types/types";
import { QueryContext } from "../App";
import { useContext } from "react";


const fallback : Outline = {
  id: 0,
  author: {
    id: 0,
    createDate: "생성일",
    modifyDate: "수정일",
    nickname: "닉네임",
    profileImgUrl: "https://phinf.pstatic.net/contact/20230727_252/1690456995185MmBBn_JPEG/image.jpg"
    },
  title: "책이 로딩중입니다...",
  published: false,
  coverImgUrl: "/img/unloaded_img.jpg",
  chapters: [
    {
      id: 0,
      title: "챕터가 로딩중입니다...",
      sortPriority: 10000,
      pages: [
        {
          id: 0,
          title: "페이지가 로딩중입니다...",
          sortPriority: 10000
        }
      ]
    }
  ]
}


// 목차정보 가져오는 api 훅
export const useGetOutlineQuery = (bookId : number) : Outline => {

  const { data, isLoading, isFetching } =  useQuery<Outline>(
    ['book', bookId], // query key

    () => getOutlineById(bookId),  // query fn

    { // options
        enabled: bookId > 0, // bookId가 0보다 클 때만 요청을 보냅니다.
    })
    
  if(data && !isLoading && !isFetching){
    return data;
  } else {
    return fallback;
  }
}


// useGetOutline 내부적으로 호출하는 axios api
const getOutlineById = async (id : number) => {

  const response = await axios.get(`/api/book/${id}/outline`);

  if(response && response.status === 200 && response.data){
    console.log("=========[Outline 데이터를 가져왔습니다]=========");
    console.log(response.data);
    console.log("============================================");
    return response.data as Outline;
  } else {
    console.log("Outline 데이터를 가져오는데 실패했습니다.")
    throw new Error("목차 정보를 가져오는데 실패했습니다.");
  }
}


// 목차정보 업데이트 api 훅
export const useRearrangeOutlineMutation = (bookId : number) : [UseMutateAsyncFunction<AxiosResponse<any, any>, unknown, Outline, unknown>, boolean] => {
    
    const {queryClient} = useContext(QueryContext);
  
    const {mutateAsync, isLoading} = useMutation(
      (newOutline : Outline) => axios.put(`/api/book/${bookId}/outline`, newOutline),
      {
        onSuccess: () => {
          queryClient.invalidateQueries(['book', bookId]);
        }
      }
    )

    return [mutateAsync, isLoading];
}