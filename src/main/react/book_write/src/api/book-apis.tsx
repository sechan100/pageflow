/* eslint-disable @typescript-eslint/no-unused-vars */
import { useQuery } from 'react-query';
import axios from 'axios';
import { BookUpdateRequest, Outline } from '../types/types';
import { useMutation } from 'react-query';
import { queryClient } from '../App';
import flowAlert from '../etc/flowAlert';


// 더미 데이터
const sampleOutline : Outline = {
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
  coverImgUrl: "https://phinf.pstatic.net/contact/20230727_252/1690456995185MmBBn_JPEG/image.jpg",
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

// useGetOutline 내부적으로 호출하는 axios api
const getOutlineById = async (id : number) : Promise<Outline> => {
  const response = await axios.get(`/api/book/${id}/outline`);

  if(response.status !== 200){
    throw new Error("책 정보를 가져오는데 실패했습니다.");
  }

  if(response.data){
    console.log("Outline Get Success!");
    console.log(response.data);
  }

  return response.data;
}

// 목차정보 가져오는 api 훅
export const useGetOutline = (bookId : number) : Outline => {

  const { data, isLoading, isFetching, error } =  useQuery<Outline>(
    
    ['book', bookId], // query key
    
    () => getOutlineById(bookId),  // query fn
    
    { // options
      enabled: bookId > 0, // bookId가 0보다 클 때만 요청을 보냅니다.
    }
  )

  // 로딩중이거나 페칭중이라면 sampleOutline을 반환
  if(isLoading || isFetching){
    return sampleOutline;
  }

  if(data){
    const outline : Outline = data;
    return outline;
  } else {
    return sampleOutline;
  }

}

// 목차정보 업데이트 api 훅
export const useRearrangeOutlineMutation = (bookId : number) => {
    
  
    const { mutateAsync, isLoading, error } = useMutation(
      (newOutline : Outline) => axios.put(`/api/book/${bookId}/outline`, newOutline),
      {
        onSuccess: () => {
          queryClient.invalidateQueries(['book', bookId]);
        }
      }
    )

    return {
      mutateAsync,
      isLoading,
      error
    }

}



export const useUpdateBook = (bookId : number) => {

  const { mutateAsync, isLoading, error } = useMutation(
    async (bookUpdateRequest : BookUpdateRequest) => {

      const formDate = new FormData();
      formDate.append("title", bookUpdateRequest.title);
      if(bookUpdateRequest.coverImg) formDate.append("coverImg", bookUpdateRequest.coverImg);


      const response = await axios.put(`/api/book/${bookId}`, formDate, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      
      if(response.status !== 200){
        flowAlert("error", "책 정보를 업데이트하는데 실패했습니다.");
        throw new Error("책 정보를 업데이트하는데 실패했습니다.");
      }

      if(response.data){
        console.log("Book Update Success!");
        console.log(response.data);
        return response.data;
      }
    },
    {
      onSuccess: (data) => {
        
        const staleBook = queryClient.getQueryData(['book', bookId]);
        
        // 클라이언트 캐시 데이터 업데이트: 서버와 재통신 하지 않고, 그냥 캐시만 낙관적으로 업데이트한다.
        if(staleBook){
          queryClient.setQueryData(['book', bookId], {
            ...staleBook,
            title: data.title,
            coverImgUrl: data.coverImgUrl
          })
        }

      }
    }
  )

  return {
    mutateAsync,
    isLoading,
    error
  }


}