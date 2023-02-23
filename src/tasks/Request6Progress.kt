package tasks

import contributors.*

//하나씩 수행됨
suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    val allUsers = mutableListOf<User>()

    for ((index, repo) in repos.withIndex()){
        val users = service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
        //개별 리포지토리의 기여자 // 전체 리포지토리의 기여자를 얻어야함
        allUsers += users
        allUsers.aggregate()
        val isCompleted = index == repos.lastIndex
        updateResults(allUsers, isCompleted)
    }
}
