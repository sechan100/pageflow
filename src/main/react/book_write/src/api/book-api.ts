/* eslint-disable @typescript-eslint/no-unused-vars */
import axios from 'axios';
import { BookUpdateRequest, Outline } from '../types/types';
import { UseMutateAsyncFunction, useMutation } from 'react-query';
import { QueryContext } from '../App';
import flowAlert from '../etc/flowAlert';
import { useContext } from 'react';



// Book 데이터 업데이트 훅
export const useUpdateBook = (bookId : number) : [UseMutateAsyncFunction<any, unknown, BookUpdateRequest, unknown>, boolean, boolean] => {

  const {queryClient} = useContext(QueryContext);

  const { mutateAsync, isLoading, isError } = useMutation(
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

  return [mutateAsync, isLoading, isError];


}