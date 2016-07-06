GrapeSwaggerRails.options.app_name = 'Nexus Api'
GrapeSwaggerRails.options.url      = '/api/docs.json'
GrapeSwaggerRails.options.before_filter_proc = proc {
  GrapeSwaggerRails.options.app_url = request.protocol + request.host_with_port
}
GrapeSwaggerRails.options.doc_expansion = 'list'
