import { createRoot } from 'react-dom/client';
import store from './redux/store.js';
import {Provider} from 'react-redux';

// Imports the necessary packages for routing with react router
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import './index.css';
import App from './App.jsx';
import ErrorPage from './pages/ErrorPage.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Login from './pages/Login.jsx';
import Signup from './pages/Singup.jsx';
import ServerDown from './pages/ServerDown.jsx';

// Defines the routes to wich the components will render
const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    errorElement:<ErrorPage />,
    children: [
      {
        index: true,
        element: <Dashboard />
      }, {
        path: '/login',
        element: < Login />
      }, {
        path: '/signup',
        element: < Signup />
      },
    ]
  }
]);


createRoot(document.getElementById('root')).render(
  <Provider store={store}> 
      <ServerDown>
        < RouterProvider router={router} />
      </ServerDown>
    </Provider>
)
