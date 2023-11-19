/* eslint-disable @typescript-eslint/no-unused-vars */
import axios from 'axios';
import { BookMutation, Outline } from '../types/types';
import { UseMutateAsyncFunction, useMutation } from 'react-query';
import { QueryContext } from '../App';
import flowAlert from '../etc/flowAlert';
import { useContext } from 'react';



// Book 데이터 업데이트 훅
export const useBookMutation = (bookId : number) : UseBookMutationReturn => {

  const {queryClient} = useContext(QueryContext);

  const { mutateAsync, isLoading, isError } = useMutation(
    async (bookMutation : BookMutation) => {

      const formDate = new FormData();
      if(bookMutation.title) formDate.append("title", bookMutation.title);
      if(bookMutation.coverImg) formDate.append("coverImg", bookMutation.coverImg);


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
        console.log("Book Update Success!", response.data);
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


export interface UseBookMutationReturn {
  mutateAsync: UseMutateAsyncFunction<void, unknown, BookMutation, unknown>;
  isLoading: boolean;
  isError: boolean;
}