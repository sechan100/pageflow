import './App.css';
import BookBasicPageForm from './components/form/BookBasicPageForm';
import { QueryClient, QueryClientProvider } from 'react-query';
import OutlineSidebar from './components/outline/OutlineSidebar';


const queryClient = new QueryClient();

function App() {



  return (
    <QueryClientProvider client={queryClient}>
      <OutlineSidebar />
      <main className="px-24 mt-16 flex-auto">
        <BookBasicPageForm />
      </main>
    </QueryClientProvider>
  );
}


export default App;