# Weher My Home

## ViewModel

- `ViewModel`

```java
class ItemViewModel : ViewModel() {
    // Observable (LiveData) 정의
    val itemLiveData = MutableLiveData<Item>()

    init {
        // 생성자에서 item이 바로 로드되도록 하거나
        // viewModel이 가진 함수를 호출하여 item이 로드되도록 하면 된다.
        // loadItem()
    }

    fun setItemId(itemId: String) {
        // DB, Http 등으로 item 로드
        Handler(Looper.getMainLooper()).postDelayed({
            DummyContent.ITEM_MAP[itemId]?.let { homeInfo ->
                itemLiveData.postValue(homeInfo)
                picDirLiveData.postValue(homeInfo.picDir)
            }
        }, 1000)
    }
}
```

- `View`

```java
class ItemFragment : Fragment() {

    companion object {
        fun newInstance() = ItemFragment()
    }

    private lateinit var viewModel: ItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.item_details_fragment, container, false)
        // ... view logics (onClick, addView, ...) ...
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // viewModel를 받아 Observable을 관측한다. (Observable 데이터가 변경되면 콜백을 받음)

        // lifecycle owner를 this로 하면 viewModel이 this의 생명주기를 따른다.
        // (fragment가 destroy되면 viewModel도 destroy)
        viewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        // Observable 데이터가 변경되면 View에 반영한다.
        viewModel.itemLiveData.observe(viewLifecycleOwner, {
            view?.findViewById<TextInputEditText>(R.id.et_name)?.text?.apply {
                clear()
                insert(0, it.name)
            }
            // ...
        })

        arguments?.run {
            getString("itemId")?.let {
                // viewModel의 item 로드
                viewModel.setItemId(it)
            }
        }
    }
}
```

## License

> 아이콘 제작자 <a href="https://www.flaticon.com/kr/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/kr/" title="Flaticon"> www.flaticon.com</a>
