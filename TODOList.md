# TACUSCI PROJECT PLAN (Detailed TODO list)

## IMPORTANT FEATURES

- [ ] Velocity template engine given access to make DB querys and execute statements
	- This will involve creating at least a few API classes which contain generic init funcs
	  like specifying the schema name and db url, but also parsing generic statements and executing them

- [ ] Insert helpful data sets into Velocity template contexts for all pages, depending on type of page
    - For example, if the page is of type forum, then provide a list of all currently logged in user names

- [ ] Create the main page management dashboard page which lists all existing pages and summerised details
    - Details include (author, last edited date/time, created date time, etc.,)
    - The same divs which are presented in a list representing each page should also be a link to edit said page