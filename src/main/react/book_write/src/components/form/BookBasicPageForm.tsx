import React, { useState } from 'react';
import { useGetBook } from '../../api/book-apis';
import { dummyBook } from '../outline/OutlineSidebar';


export default function BookBasicPageForm() {


  // function changeBookTitle(titleInput : any) {
  //   setBook({
  //     ...book,
  //     title: titleInput.target.value
  //   })
  // }

  // const book = dummyBook;
  const { data: book, isLoading, isFetching } = useGetBook(1);

  if (isLoading) {
    return <div>Loading...</div>
  }

  if (isFetching) {
    return <div>Updating...</div>
  }

  return (
    <>
      <form action="#">
        <div className="sm:col-span-2">
            <label htmlFor="title" className="block mb-2 text-sm font-medium text-gray-900">책 체목</label>
            <input value={book?.title} type="text" name="title" id="title" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="책 제목을 입력해주세요." />
        </div>
        <div className="w-full">
            <label htmlFor="brand" className="block mb-2 text-sm font-medium text-gray-900">Brand</label>
            <input type="text" name="brand" id="brand" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="Product brand" />
        </div>
        <div className="w-full">
            <label htmlFor="price" className="block mb-2 text-sm font-medium text-gray-900">Price</label>
            <input type="number" name="price" id="price" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="$2999" />
        </div>
        <div>
            <label htmlFor="item-weight" className="block mb-2 text-sm font-medium text-gray-900">Item Weight (kg)</label>
            <input type="number" name="item-weight" id="item-weight" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="12" />
        </div> 
        <button type="submit" className="inline-flex items-center px-5 py-2.5 mt-4 sm:mt-6 text-sm font-medium text-center text-white bg-primary-700 rounded-lg focus:ring-4 focus:ring-primary-200 hover:bg-primary-800">
            Add product
        </button>
      </form>
    </>
  );
}