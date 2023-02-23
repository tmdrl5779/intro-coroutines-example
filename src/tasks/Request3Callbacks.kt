package tasks

import contributors.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

// contribs1을 호출한 뒤 callback을 기다리지 않고 contribs2를 수행 가능
// -> Retrofit이용
fun loadContributorsCallbacks(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    val counter = AtomicInteger(0)
    service.getOrgReposCall(req.org).onResponse { responseRepos ->
        logRepos(req, responseRepos)
        val repos = responseRepos.bodyList()
        val allUsers = mutableListOf<User>()
        println("1111111111111111111")
        for (repo in repos) {
            println("222222222222222222")
            //retrofit2를 이용한 http통신 -> background Thread에서 실행됨, 콜백을 통해 Main Thread에 값 업데이트
            service.getRepoContributorsCall(req.org, repo.name).onResponse { responseUsers ->
                logUsers(repo, responseUsers)
                val users = responseUsers.bodyList()
                allUsers += users
                println("3333333333333333333")
                if(counter.incrementAndGet() == repos.size){
                    updateResults(allUsers.aggregate())
                }
            }
        }
        println("4444444444444444444444")
        // TODO: Why this code doesn't work? How to fix that?
        updateResults(allUsers.aggregate())
    }
}

inline fun <T> Call<T>.onResponse(crossinline callback: (Response<T>) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            callback(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            log.error("Call failed", t)
        }
    })
}
