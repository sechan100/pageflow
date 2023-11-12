/* eslint-disable @typescript-eslint/no-unused-vars */
import { useQuery } from 'react-query';
import axios from 'axios';
import { Outline } from '../types/types';


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




const getOutlineById = async (id : number) : Promise<Outline> => {
  const response = await axios.get(`/api/book/outline/${id}`);

  if(response.status !== 200){
    throw new Error("책 정보를 가져오는데 실패했습니다.");
  }

  if(response.data){
    console.log(response.data);
  }

  return response.data;
}


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