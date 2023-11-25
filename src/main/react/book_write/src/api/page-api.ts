import { UseMutateAsyncFunction, useMutation, useQuery } from "react-query"
import { IPage, PageMutation } from "../types/types"
import axios from "axios";
import { QueryContext } from "../App";
import { useContext } from "react";
import flowAlert from "../etc/flowAlert";


const fallback : IPage = {
  id: 0,
  title: "페이지가 로딩중입니다...",
  content: "페이지가 로딩중입니다...",
}


// 페이지 정보 업데이트
export const usePageMutation = () => {

  const {queryClient, bookId} = useContext(QueryContext);

  const { mutateAsync, isLoading, isError } = useMutation(
    async (pageMutations : PageMutation[]) => {


      const response = await axios.put(`/api/book/${bookId}/chapter/pages`, pageMutations, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      if(response.status !== 200){
        flowAlert("error", "페이지 정보를 업데이트하는데 실패했습니다.");
        throw new Error("페이지 정보를 업데이트하는데 실패했습니다.");
      }

      if(response.data){
        console.log("Page Update Success!", response.data);
      }
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['book', bookId]);
      }
    }
  )

  return { mutateAsync, isLoading, isError };


}

// 새로운 페이지 생성
export const useCreatePageMutation = (bookId : number, chapterId : number) : { mutateAsync: UseMutateAsyncFunction<void, unknown, void, unknown>, isLoading: boolean, isError: boolean} => {

  const {queryClient} = useContext(QueryContext);

  const { mutateAsync, isLoading, isError } = useMutation(
    async () => {

      const response = await axios.post(`/api/book/${bookId}/chapter/${chapterId}/page`);
      
      if(response.status !== 200){
        throw new Error("페이지를 생성하는데 실패했습니다.");
      }

      if(response.data){
        console.log("Sercer Response: create new page", response.data);
      }
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['book', bookId]);
      }
    }
  )

  return {mutateAsync, isLoading, isError};
}

// 페이지 정보 가져오기
export const useGetPageQuery = (pageId : number) => {

  const { bookId } = useContext(QueryContext);
  
  const { data, isLoading, isFetching } =  useQuery<IPage>(
    ['page', pageId], // query key

    () => getPageById(bookId, pageId),  // query fn

    { // options
        enabled: pageId > 0, // pageId가 0보다 클 때만 요청을 보냅니다.
    });

  if(data && !isLoading && !isFetching){
    return data;
  } else {
    return fallback;
  }
}










// 페이지 정보 가져오는 axios api
// react query api 훅에서 내부적으로 호출
async function getPageById(bookId : number, pageId : number) : Promise<IPage> {
  const response = await axios.get(`/api/book/${bookId}/chapter/page/${pageId}`);

  if(response && response.status === 200 && response.data){
    console.log("====[ Success to Fetching Page ]====", response.data);
    return response.data as IPage;
  } else {
    console.log("Page 데이터를 가져오는데 실패했습니다.")
    throw new Error("Page 정보를 가져오는데 실패했습니다.");
  }
}