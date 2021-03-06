package com.example.telstrapoc.utils

import com.example.telstrapoc.executer.IExecuterThread
import com.example.telstrapoc.executer.UIThread
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.subscriptions.Subscriptions

/*
base usecase class to call the API using RX Java
 */
abstract class UseCase<T>(executorThreadI: IExecuterThread, postExecutionThread: UIThread) :
    BaseInteractor<T> {
    private var executorThreadI: IExecuterThread? = null
    private var postExecutionThread: UIThread? = null
    private var subscription: Subscription? = Subscriptions.empty()

    init {
        this.executorThreadI = executorThreadI
        this.postExecutionThread = postExecutionThread
    }

    /**
     * Builds an {@link Observable} which will be used when executing the current {@link UseCase}.
     */
    abstract fun createObservable(): Observable<T>

    /*
    API call
    subscribe on executorThread(background) thread
    observes on postExecutionThread (main) thread
     */
    override fun execute(subscriber: Subscriber<T>) {
        this.subscription = createObservable()
            .subscribeOn(executorThreadI!!.getScheduler())
            .observeOn(postExecutionThread!!.getScheduler())
            .subscribe(subscriber)
    }

    /**
     * Unsubscribes from current {@link Subscription}.
     */
    fun unsubscribe() {
        if (!subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
    }
}
