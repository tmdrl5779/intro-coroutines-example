package tasks

import contributors.GitHubService
import contributors.RequestData
import contributors.User
import kotlin.concurrent.thread

//다른 쓰레드에서 수행시킴
//네트워크 호출 중에도 UI는 멈추지 않음
//callback 함수로 result update
fun loadContributorsBackground(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    thread {
        val users = loadContributorsBlocking(service, req)
        updateResults(users)
    }
}