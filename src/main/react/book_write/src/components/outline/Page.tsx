import React from 'react';
import {PageSummary} from '../../types/types'
import { Link } from 'react-router-dom';

interface pageProps {
  page: PageSummary;
}

export default function Page(props : pageProps){
  return (
    <li>
      <Link to={"/page/" + props.page.id} className="flex bg-gray-800 p-1 items-center pl-6 w-full text-white text-sm font-normal text-gray-900 rounded-lg transition duration-75 group hover:bg-gray-700">
        {props.page.title}
      </Link>
    </li> 
  );
}