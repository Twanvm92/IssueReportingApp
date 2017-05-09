# Git commands conventions

## Push changes to repo

```git
git add *
```
Add all files and directories (which are not included in the .gitignore file) that has changed to your new commit.

```git
git commit -m "Commit message"
```
Use the commit message for a brief summary of what you changed in this commit since your last edit.

```git
git push origin branchname
```
Use this to push your changes to the branch specified in __branchname__.
### NEVER PUSH IN THE MASTER BRANCH!


## Pull changes from repo

```git
git pull origin branchname
```
Use this to pull the changes from the remote repo to your local repo.


## Branches

```git
git branch
```
Shows what branch you are currently on and what branches are available.
### Make sure you are **not** in the master branch before you make changes to the project.

```git
git checkout branchname
```
Switch to branch __branchname__.


## Merging

When a feature/activity is done, it needs to be merged with another branch. This will be the RC (Release Candidate) branch. Follow the steps below to do so:
1. Commit all changes to the development repo you are currently in.
2. Make sure everything is tested thorough.
3. Create a new pull request on the Github repo. Select the latest commit and hope for the sake of easyness that Github gives you a green checkmark that merging can be done automatically...
4. Save the pull request and let the team know you made one.
5. It will be checked by the project leader / someone who is good in git and will be finally merged. **Do not merge it on your own.**
6. A release candidate will get a version number and will be extra tested. If done, it will be merged with the master branch by someone who is good with git / the project leader.


## Other conventions

1. As said before, **NEVER WORK IN THE MASTER BRANCH.**
2. Use the dev branch for general development stuff, e.g. readme files.
3. Create a separate branch for a feature, e.g. account branch if you're working on accounts. This keeps several different features split.
4. If you don't know something or think you've messed something up, ask the group.
5. **NEVER WORK IN THE MASTER BRANCH.**
6. Each commit in the master branch (hopefully these are only merges...) is worth a party and beer.
7. For issues, use the issue tracker on Github. To close issues, include this in a commit: __fixes #1__ where #1 is the issue ID.