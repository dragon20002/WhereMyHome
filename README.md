# Weher My Home

집 메모하는 어플

## 화면 설명

### 1. 집 목록 화면

- 집 이름/보증금/월세/관리비/Q&A점수 정보를 표시

- 수정(🖍) 버튼을 눌러 항목 수정

- 추가(➕) 버튼을 눌러 새로운 집 정보 추가

  ![목록](readme_img/1-home-list.jpg)

### 2. 집 상세 화면

- 카메라(📷) 버튼을 눌러 새로운 사진 찍기

- 사진을 눌러 전체화면으로 보기

- 지도(🗺) 버튼을 눌러 현재 위치 가져오기

- 뒤로가기 버튼을 누르면 수정사항이 저장됨

  ![상세](readme_img/2-home-details.jpg)


### 3. 사진 전체화면

- 뒤로가기나 화면 터치 후 표시되는 '닫기' 버튼을 눌러 나감

  ![전체화면](readme_img/3-full-screen-img.jpg)

### 4. Q&A 작성

- 입력은 +-정수와 참거짓

- 비고(···) 버튼을 눌러 비고 항목 작성하는 팝업 노출

  ![QandA](readme_img/4-home-qanda.png)  

### 5. 비고 작성 팝업

- 작성 후 저장

  ![remark](readme_img/5-home-remark.png)

### 6. 현재 위치 지도

- 현재 위치를 표시함

- 뒤로가기를 누르면 현재 위치의 주소를 상세화면의 '위치' 입력란에 저장

  ![map](readme_img/6-home-location-map.png)

## 개선할 점

- Q&A 점수가 항목별 가중치가 없어 의미없음

- 지도(🗺) 버튼을 누르면 바로 현재 위치를 찾기 때문에 이전에 입력했던 정보가 지워짐. 지도 화면 내부에 '현재위치 찾기' 버튼을 별도 추가 필요

- 그리고 가장 중요한 실사용 후기... 대충 만들어서 실제 사용해봤는데 방을 금방금방 보다보니 조목조목 체크하기 귀찮아져서 실제 쓸모는 없을 것 같다.

## ViewModel

- `ViewModel`

```java
class ItemViewModel : ViewModel() {
    /**
     * db 사용할 경우 RoomDatabase 구현체나 Dao를 선언한다.
     *
     * view model과 분리해서 조회 결과만 setter로 넣어줘 '결합도'를 낮출 수 있지만...
     * 귀찮아서 그냥 view model 안에다 선언함
     */
    var db: AppDatabase? = null

    /**
     * Observable (LiveData) 정의
     *
     * 데이터소스(DB, Network 등)로부터 viewModel 값을 받아오는 역할을 한다.
     * LiveData가 값을 받아오면 observer들은 뷰에 값을 갱신하는 작업을 해주면 된다.
     */
    val itemLiveData = MutableLiveData<Item>()

    /**
     * Observable 정의
     * 
     * ObservableXXX는 값이 변경되면 연결된 layout view를 갱신하는 역할을 한다.
     */
    val name = ObservableField<String>()
    val address = ObservableField<String>()
    val deposit = ObservableField<String>()
    val rental = ObservableField<String>()
    val expense = ObservableField<String>()
    val startDate = ObservableField<String>()
    val endDate = ObservableField<String>()
    val pictureList: ObservableList<DummyContent.Picture> = ObservableArrayList()
    val qandaList: ObservableList<DummyContent.QandaViewData> = ObservableArrayList()

    fun init(db: AppDatabase) {
        this.db = db
    }

    // db에서 id로 item 조회
    fun setItemId(itemId: String) {
        Thread { // item 로드
            db?.homeInfoDao()?.loadAllByIds(arrayOf(itemId))?.takeIf {
                it.isNotEmpty()
            }?.get(0)?.let { homeInfo ->
                itemLiveData.postValue(homeInfo)
            }
        }.start()
    }

    // db에 item 저장/수정
    suspend fun saveItem(): Item { ... }
}
```

- `View`

```java
class ItemFragment : Fragment() {
    ...
    private var viewModel: ItemViewModel? = null
    private var binding: ItemFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_fragment, container, false)
        val view = binding?.root

        ...view?.findViewById....

        // 기존 viewModel이 있으면 그대로 사용
        if (viewModel != null) {
            viewModel = ViewModelProvider(this).get(ItemViewModel::class.java).apply {
                init(AppDatabase.getDatabase(requireContext()))
                itemLiveData.removeObservers(viewLifecycleOwner)

                // db -> liveData
                itemLiveData.observe(viewLifecycleOwner, { it ->
                    // [dataBinding 사용] liveData -> observable -> view 적용
                    name.set(it.name)
                    address.set(it.address)
                    deposit.set(it.deposit.toString())
                    rental.set(it.rental.toString())
                    expense.set(it.expense.toString())
                    startDate.set(it.startDate)
                    endDate.set(it.endDate)
                    pictureList.addAll(it.pictures)
                    qandaList.addAll(it.qandas)

                    // [dataBinding 사용안하는 경우] 뷰에 직접 넣어줘야 한다
                    // ...findViewById(...)?.setText(it.name)...
                })
            }
        }

        binding?.viewModel = viewModel

        // 초기화
        if (arguments == null) { // Add Mode
            viewModel?.itemLiveData?.postValue(createDummyItem())
            viewModel?.pictureList?.addAll(ArrayList())
            viewModel?.qandaList?.addAll(createDummyQandaList())
        } else { // Edit Mode
            arguments?.getString("itemId")?.let {
                viewModel?.setItemId(it)
            }
        }

        return view
    }

    fun someFunction() {
        // 뷰 입력값 변경하려면...
        // android:text="@={item.name}"
        viewModel?.name?.set("change it")

        // 뷰 입력값 조회
        // android:text="@{item.name}" // 단방향 바인딩 (사용자가 수정한 값은 조회불가)
        // android:text="@={item.name}" // 양방향 바인딩 (사용자가 수정한 값은 조회가능)
        viewModel?.name?.get()

        // 리스트 항목 추가사항을 뷰에 적용 하려면...
        // [listAdapter 사용 시] <ListView android:adapter=@{pictureListAdapter} ...>
        // [BindingAdapter 사용 시] @BindingAdapter 함수를 정의하여 직접 바인딩
        viewModel?.pictureList?.add(...)
    }
}
```

## NavController

- Fragment 간 이동을 @navigation/nav_graph.xml에 명시적으로 작성하여 FragmentTransaction 보다 쉽고 편하게 관리할 수 있다.

- `@navigation`

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/ItemFragment">

    <fragment
        android:id="@+id/ItemFragment"
        android:name="com.minuminu.haruu.wheremyhome.ItemFragment"
        android:label="@string/item_fragment_label"
        tools:layout="@layout/item_fragment">

        <action
            android:id="@+id/action_ItemFragment_to_PictureFullScreenFragment"
            app:destination="@id/PictureFullScreenFragment" />
        <action
            android:id="@+id/action_ItemFragment_to_MapsFragment"
            app:destination="@id/MapsFragment" />
    </fragment>
    <fragment
        android:id="@+id/PictureFullScreenFragment"
        android:name="com.minuminu.haruu.wheremyhome.PictureFullscreenFragment"
        android:label="전체화면"
        tools:layout="@layout/fragment_picture_fullscreen">
    </fragment>
    <fragment
        android:id="@+id/MapsFragment"
        android:name="com.minuminu.haruu.wheremyhome.MapsFragment"
        android:label="지도"
        tools:layout="@layout/fragment_maps" />
```

- `이동`

```java
import androidx.navigation.fragment.findNavController
...
findNavController().navigate(
    R.id.action_ItemFragment_to_MapsFragment,
    Bundle().apply {
        putString("address", address)
    })
```

- `이전 Fragment로 이동`

```java
findNavController().popBackStack(R.id.ItemFragment, false)
```

- `Bundle 데이터 변경 리스너`

```java
findNavController().addOnDestinationChangedListener { _, dest, args ->
    when (dest.id) {
        R.id.MapsFragment -> {
            args?.getString("address")?.let { address ->
                Log.d(javaClass.name, "address - observe $address")
            }
        }
    }
}
```

- `Bundle 데이터 변경`

```java
// Notify data changed
arguments?.putString("address", address)
```

## License

> 앱 아이콘 제작자 <a href="https://www.flaticon.com/kr/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/kr/" title="Flaticon"> www.flaticon.com</a>

> <a href="https://www.flaticon.com/authors/dmitri13" title="dmitri13">dmitri13</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>