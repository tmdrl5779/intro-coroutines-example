package tasks

import contributors.*
import kotlinx.coroutines.*

// async -> deferred object를 return -> await 사용가능
// launch -> job을 return -> join 사용가능
// coroutineScope가 감싸고 있기 때문에 return 사용불가, 마지막 값이 반환
suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    repos.map { repo ->
        async {
            log("starting loading for ${repo.name}")
            delay(3000)
            service
                .getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }.awaitAll().toList().flatten().aggregate()

    // async : List<User> -> repos.map : List<List<User>> -> flatten() -> List<User>
}