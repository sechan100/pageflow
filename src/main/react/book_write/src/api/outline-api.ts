import axios, { AxiosResponse } from "axios";
import { UseMutateAsyncFunction, useMutation, useQuery } from "react-query";
import { Outline, OutlineMutation } from "../types/types";
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
  ],
  preferenceStatistics: {
    like: 0,
    dislike: 0
  }
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

  const response = await axios.get(`/api/books/${id}/outline`);

  if(response && response.status === 200 && response.data){
    console.log("====[ Success to Fetching Outline ]====", response.data);
    return response.data as Outline;
  } else {
    console.log("Outline 데이터를 가져오는데 실패했습니다.")
    throw new Error("목차 정보를 가져오는데 실패했습니다.");
  }
}


// 목차정보 업데이트 api 훅
export const useOutlineMutation = (bookId : number) : UseOutlineMutationReturn => {
    
    const {queryClient} = useContext(QueryContext);
  
    const { mutateAsync, isLoading, isError } = useMutation(
      (outlineUpdateBody : OutlineMutation) => axios.put(`/api/books/${bookId}/outline`, outlineUpdateBody),
      {
        onSuccess: () => {
          queryClient.invalidateQueries(['book', bookId]);
        }
      }
    )

    return {mutateAsync, isLoading, isError};
}


export interface UseOutlineMutationReturn {
  mutateAsync: UseMutateAsyncFunction<AxiosResponse<any, any>, unknown, OutlineMutation, unknown>;
  isLoading: boolean;
  isError: boolean;
}