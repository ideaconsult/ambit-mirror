# Contributor guide
## Branching
- Branching is loosely based on the [A successful Git branching model](https://nvie.com/posts/a-successful-git-branching-model/) article.
- `origin/develop` is the main, “integration”, “current”, or “snapshot” branch, where almost all new code lands.
- `origin/master` is the latest “production-ready” code and what typically runs on IDEAconsult's servers.
## Rules
### Committing code
- Do **not** commit directly to `origin/master` and avoid committing directly to `origin/develop` (urgent bugfixes to `origin/develop` may be acceptable, provided that they are clearly communicated to all developers).
- Create new branches for each independent new feature or bugfix that you work on. Try to keep all changes in such branch “on topic”. In particular, avoid mixing completely unrelated changes in a single branch.
- Consider using names for the branches that describe their topic, e.g. `improved_csv_loading_v2` (a second attempt at importing the import of CSV files) or `fix_inconsistent_dependency_versions`. Referencing issue tickets in the branch name is especially helpful, e.g. `T238_fix_http_error_500` or even just `T238`, where 238 is the ticket or issue number. As a rule, avoid generic names like `my_branch`, `fixes`, or `new_features`.
- Do **not** merge `origin/master` or `origin/develop` into your local branch. Always [rebase](https://git-scm.com/book/en/v2/Git-Branching-Rebasing) instead. See [Avoiding automatic merges on pull](#avoiding-automatic-merges-on-pull) for information on how to avoid Git performing automatic merge on `git pull`.
- After a branch has been merged successfully, delete it. Don't let unnecessary branches accumulate.
### Code style
* Prefer using code editors or IDEs that support [EditorConfig](https://editorconfig.org/). Consult the sections "No Plugin Necessary" and "Download a Plugin" on https://editorconfig.org/ to identify appropriate ones.
* If possible, pass all your code through an appropriate linter. For Java, see for example [this list](https://lightrun.com/java/top-10-java-linters/).
## Quick Reference
### Cloning and pulling
- R/W clone
```
git clone ssh://USERNAME@git.code.sf.net/p/ambit/git ambit
cd ambit
```

- pulling latest changes from the central repo (attempting to also rebase all local commits)
```
git pull --rebase
```

### Branching and rebasing
- checking what branches are available and what is the active one
```
git branch -avv
```

- creating a new feature or bugfix branch
```
git checkout develop
git pull
git checkout -b NAMEOFTHEBRANCH
```

- rebasing the working branch on `origin/develop`
```
git fetch origin develop:develop
git rebase develop
```

- same, but interactively, allowing reordering, squashing, and editing commits
```
git fetch origin develop:develop
git rebase -i develop
```

### Pushing and merging
- first-time pushing the working branch to the central repo (use just `git push` afterwards)
```
git push -u origin NAMEOFTHEBRANCH
```

- merging the finalised branch to `origin/develop`
```
git checkout NAMEOFTHEBRANCH
git pull
git checkout develop
git pull
git merge --no-ff NAMEOFTHEBRANCH
```

- merging `origin/develop` to `origin/master`
```
git checkout develop
git pull
git checkout master
git pull
git merge --no-ff develop
```

### Cleaning up
- deleting a merged local branch
```
git branch -d NAMEOFTHEBRANCH
```

- deleting a remote branch
```
git push --delete origin NAMEOFTHEBRANCH
```

- force-deleting an unmerged branch (**DANGEROUS!** Make sure you don't need it as there's no turning back!)
```
git branch -D NAMEOFTHEBRANCH
```

## Miscellanea
### Avoiding automatic merges on pull

To avoid the undesired automatic merge when performing a `git pull` on a branch like `origin/master` or `origin/develop`, set the following Git option in one of the two ways:

* for this repo only (run after cd'ing to the repo directory):
```sh
git config pull.ff only
```

* for all Git repos (run in any directory):
```sh
git config --global pull.ff only
```

After setting the option, you may see the following message when pushing local commits:
```
fatal: Not possible to fast-forward, aborting.
```

This means there are remote commits that you don't have locally. To attempt automatic rebasing:
```
git pull --rebase
```

If there are conflicting changes, you will need to first resolve the conflicts (see [git rebase](https://git-scm.com/docs/git-rebase)).

Without these steps, recent versions of Git may produce the following warning on `git pull`:
```
hint: Pulling without specifying how to reconcile divergent branches is
hint: discouraged. You can squelch this message by running one of the following
hint: commands sometime before your next pull:
hint:
hint:   git config pull.rebase false  # merge (the default strategy)
hint:   git config pull.rebase true   # rebase
hint:   git config pull.ff only       # fast-forward only
hint:
hint: You can replace "git config" with "git config --global" to set a default
hint: preference for all repositories. You can also pass --rebase, --no-rebase,
hint: or --ff-only on the command line to override the configured default per
hint: invocation.
```
