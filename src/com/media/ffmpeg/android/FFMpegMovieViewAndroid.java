package com.media.ffmpeg.android;

import java.io.IOException;
import java.util.List;

import com.media.ffmpeg.FFMpegPlayer;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class FFMpegMovieViewAndroid extends SurfaceView {
	private static final String 	TAG = "FFMpegMovieViewAndroid"; 
	
	private Context					mContext;
	private FFMpegPlayer			mPlayer;
	private SurfaceHolder			mSurfaceHolder;
	private MediaController			mMediaController;
	private SurfaceView				mSurfaceView;
	
	Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;
	
	
	public FFMpegMovieViewAndroid(Context context,SurfaceView surfaceView) {
        super(context);
        mSurfaceView = surfaceView;
      //  mCamera = android.hardware.Camera.open();
      //  mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
      //  requestLayout();
       
        initVideoView(context);
    }
	
	
	public FFMpegMovieViewAndroid(Context context) {
        super(context);
        initVideoView(context);
    }
    
    public FFMpegMovieViewAndroid(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initVideoView(context);
    }
    
    public FFMpegMovieViewAndroid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView(context);
    }
    
    private void initVideoView(Context context) {
    	mContext = context;
    	mPlayer = new FFMpegPlayer();
    	mSurfaceView.getHolder().addCallback(mSHCallback);
    	//getHolder().addCallback(mSHCallback);
    }
    
    public void onBackPressed ()
    {
    	release();
    }
    public void SetSurfaceVideo (SurfaceView surfaceView)
    {
    	mSurfaceView = surfaceView;
    }
    
    private void attachMediaController() {
    	mMediaController = new MediaController(this.getContext());
        View anchorView = this.getParent() instanceof View ?
                   // (View)this.getParent() : this;
        		 mSurfaceView : mSurfaceView;
        mMediaController.setMediaPlayer(mMediaPlayerControl);
        mMediaController.setAnchorView(anchorView);
        mMediaController.setEnabled(true);
    }
    
    public void setVideoPath(String filePath) throws IllegalArgumentException, IllegalStateException, IOException {
		mPlayer.setDataSource(filePath);
	}
    
    /**
     * initzialize player
     */
    private void openVideo() {
    	try {
    		mPlayer.setDisplay(mSurfaceHolder);
			mPlayer.prepare();
		} catch (IllegalStateException e) {
			Log.e(TAG, "Couldn't prepare player: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "Couldn't prepare player: " + e.getMessage());
		}
    }
    
    private void startVideo() {
    	attachMediaController();
    	mPlayer.start();
    }
    
    private void release() {
    	Log.d(TAG, "releasing player");
    	
    	mPlayer.suspend();
		
		Log.d(TAG, "released");
    }
    
    public void pause() {
		mPlayer.suspend();
	}
    
    public void start() {
		mPlayer.start();
	}
    
    public boolean isPlaying() {
		return mPlayer.isPlaying();
	}
    
    
    public boolean onTouchEvent(android.view.MotionEvent event) {
    	if(!mMediaController.isShowing()) {
			mMediaController.show(3000);
		}
		return true;
    }
 /*   
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && ((ViewGroup) super.getRootView()).getChildCount() > 0) {
            final View child = ((ViewGroup) super.getRootView()).getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }
    
    
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
  */  
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            startVideo();
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            openVideo();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
			release();
			if(mMediaController.isShowing()) {
				mMediaController.hide();
			}
			// after we return from this we can't use the surface any more
            mSurfaceHolder = null;
        }
       
    };
    
    MediaPlayerControl mMediaPlayerControl = new MediaPlayerControl() {
		
		public void start() {
			mPlayer.start();
		}
		
		public void seekTo(int pos) {
			//Log.d(TAG, "want seek to");
		}
		
		public void pause() {
			mPlayer.suspend();
		}
		
		public boolean isPlaying() {
			return mPlayer.isPlaying();
		}
		
		public int getDuration() {
			return mPlayer.getDuration();
		}
		
		public int getCurrentPosition() {
			return mPlayer.getCurrentPosition();
		}
		
		public int getBufferPercentage() {
			//Log.d(TAG, "want buffer percentage");
			return 0;
		}

		@Override
		public boolean canPause() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean canSeekBackward() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean canSeekForward() {
			// TODO Auto-generated method stub
			return false;
		}
		
	};
}
