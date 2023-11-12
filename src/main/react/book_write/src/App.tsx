import './App.css';
import BookBasicPageForm from './components/form/BookBasicPageForm';
import { QueryClient, QueryClientProvider } from 'react-query';
import OutlineSidebar from './components/outline/OutlineSidebar';
import { useState } from 'react';


function App() {

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [bookId, setBookId] : [number, any] = useState(2);

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

  const drillingProps = {
    bookId: bookId,
    queryClient: queryClient
  }


  return (
    <QueryClientProvider client={queryClient}>
      <OutlineSidebar {...drillingProps} />
      <main className="px-24 mt-16 flex-auto">
        <BookBasicPageForm {...drillingProps}/>
      </main>
    </QueryClientProvider>
  );
}


export default App;