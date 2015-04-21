Programs sends file names.txt from /cloud dir to client. 
(file name and path is hardcoded, but could be simple extended to program args)

1. Build with make:
shell: make

2. Run:
	a) server:
	shell: java FileServer

	b) client:
	open new terminal: (ctrl+shift+t) 
	shell: ./file_client.out

You should see names.txt file in your working directory.
