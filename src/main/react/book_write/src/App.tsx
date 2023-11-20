import './App.css';
import { QueryClient, QueryClientProvider } from 'react-query';
import { useState } from 'react';
import BookEntityDraggableContext from './components/BookEntityDraggableContext';
import 'react-image-crop/dist/ReactCrop.css';
import React from 'react';


const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnMount: false,
      refetchOnWindowFocus: false,
      retry: false,
      staleTime: 1000 * 60 * 60 // 1시간
    }
  }
});

export const QueryContext = React.createContext({queryClient, bookId: 0});


function App() {

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [bookId, setBookId] : [number, any] = useState(2);


  return (
    <QueryClientProvider client={queryClient}>
      <QueryContext.Provider value={{queryClient, bookId}}>
        <BookEntityDraggableContext />
      </QueryContext.Provider>
    </QueryClientProvider>
  );
}


export default App;