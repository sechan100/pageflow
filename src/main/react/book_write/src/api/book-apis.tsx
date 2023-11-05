import { useQuery } from 'react-query';
import axios from 'axios';
import { IBook } from '../types/book';



const getBookById = async (id : number) : Promise<IBook> => {
  const response = await axios.get(`/api/book?id=${id}`);
  return response.data;
}




export const useGetBook = (bookId : number) => {
  return useQuery<IBook>(
    
    ['book', bookId], // query key
    
    () => getBookById(bookId),  // query fn
    
    { // options
    onSuccess: (data) => { console.log(data); },
    refetchOnWindowFocus: false
    }
  )
} 