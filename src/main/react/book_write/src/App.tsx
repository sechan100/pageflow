import './App.css';
import { QueryClient, QueryClientProvider } from 'react-query';
import { useState } from 'react';
import BookEntityDraggableContext from './components/BookEntityDraggableContext';


export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnMount: false,
      refetchOnWindowFocus: false,
      retry: false,
      staleTime: 1000 * 60 * 60 // 1시간
    }
  }
});


function App() {

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [bookId, setBookId] : [number, any] = useState(2);

  const drillingProps = {
    bookId: bookId,
    queryClient: queryClient
  }


  return (
    <QueryClientProvider client={queryClient}>
      <BookEntityDraggableContext {...drillingProps} />
    </QueryClientProvider>
  );
}


export default App;