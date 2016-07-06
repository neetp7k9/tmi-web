Rails.application.routes.draw do
  get 'admin/reports'

  resources :images
  resources :documents
  resources :projects
  get 'searches/:id' => 'search#show'
  get 'search_results/:id' => 'search_result#show'
  root to: "home#home"
  get 'home/home'

  mount API::Endpoints => '/api'
  mount GrapeSwaggerRails::Engine => '/documentation'
  
  get "logout" => "home#logout"
  devise_for :users
  resources :project
  resources :document
  resources :image

  get '/crop_to_search' => 'home#crop_to_search'
  get '/home/index' => 'home#index'
  get '/crop' => 'home#crop'
  post '/search_by_document' => 'home#search_by_document'
  post '/search_by_project' => 'home#search_by_project'
  post '/crop' => 'home#crop'

  # The priority is based upon order of creation: first created -> highest priority.
  # See how all your routes lay out with "rake routes".

  # You can have the root of your site routed with "root"
  # root 'welcome#index'

  # Example of regular route:
  #   get 'products/:id' => 'catalog#view'

  # Example of named route that can be invoked with purchase_url(id: product.id)
  #   get 'products/:id/purchase' => 'catalog#purchase', as: :purchase

  # Example resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Example resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Example resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Example resource route with more complex sub-resources:
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', on: :collection
  #     end
  #   end

  # Example resource route with concerns:
  #   concern :toggleable do
  #     post 'toggle'
  #   end
  #   resources :posts, concerns: :toggleable
  #   resources :photos, concerns: :toggleable

  # Example resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end
end
